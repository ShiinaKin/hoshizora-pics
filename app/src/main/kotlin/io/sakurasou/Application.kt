package io.sakurasou

import io.ktor.server.application.*
import io.sakurasou.config.configureDatabase
import io.sakurasou.config.configureJwt
import io.sakurasou.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val baseUrl = environment.config.property("ktor.application.base-url").getString()

    val redisHost = environment.config.property("ktor.application.cache.redis.host").getString()
    val redisPort = environment.config.property("ktor.application.cache.redis.port").getString()

    configureDatabase()
    configureJwt()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureSwagger(baseUrl)
    configureCache(redisHost, redisPort)
}
