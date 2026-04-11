package io.sakurasou.hoshizora

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.cors.routing.CORS
import io.sakurasou.hoshizora.config.configureDatabase
import io.sakurasou.hoshizora.config.configureJwt
import io.sakurasou.hoshizora.di.InstanceCenter
import io.sakurasou.hoshizora.listener.TaskListener
import io.sakurasou.hoshizora.plugins.configureCache
import io.sakurasou.hoshizora.plugins.configureHTTP
import io.sakurasou.hoshizora.plugins.configureMonitoring
import io.sakurasou.hoshizora.plugins.configureRouting
import io.sakurasou.hoshizora.plugins.configureSecurity
import io.sakurasou.hoshizora.plugins.configureSerialization
import io.sakurasou.hoshizora.plugins.configureSwagger
import io.sakurasou.hoshizora.plugins.generateOpenApiJson
import kotlin.io.path.Path
import kotlin.io.path.exists

fun main(args: Array<String>) {
    EngineMain
        .main(args)
}

fun Application.swaggerModule() {
    val baseUrl = environment.config.property("ktor.application.base-url").getString()
    configureSwagger(baseUrl)
}

fun Application.mainModule() {
    val imageMagickLibPath = environment.config.property("native.imagemagick.magickwand.lib").getString()
    if (!Path(imageMagickLibPath).exists()) throw IllegalArgumentException("Image Magick Lib does not exist")

    val redisHost = environment.config.property("ktor.application.cache.redis.host").getString()
    val redisPort = environment.config.property("ktor.application.cache.redis.port").getString()

    val clientTimeout =
        environment.config
            .property("client.timeout")
            .getString()
            .toLong()
    val clientProxyAddress = environment.config.property("client.proxy.address").getString()

    InstanceCenter.init(imageMagickLibPath)
    InstanceCenter.initClient(clientTimeout, clientProxyAddress)

    configureDatabase()
    configureCache(redisHost, redisPort)
    configureJwt()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    TaskListener.startListening()

    if (developmentMode) {
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Post)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowCredentials = true
            anyHost()
        }
        swaggerModule()
        generateOpenApiJson()
    }
}
