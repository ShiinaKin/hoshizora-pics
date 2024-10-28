package io.sakurasou

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.sakurasou.config.configureDatabase
import io.sakurasou.config.configureJwt
import io.sakurasou.di.InstanceCenter
import io.sakurasou.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.swaggerModule() {
    val baseUrl = environment.config.property("ktor.application.base-url").getString()
    configureSwagger(baseUrl)
}

fun Application.mainModule() {

    val redisHost = environment.config.property("ktor.application.cache.redis.host").getString()
    val redisPort = environment.config.property("ktor.application.cache.redis.port").getString()

    InstanceCenter.initDao()
    InstanceCenter.initService()
    configureDatabase()
    InstanceCenter.initSystemStatus()
    InstanceCenter.initRolePermissions()
    configureCache(redisHost, redisPort)
    configureJwt()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()

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
