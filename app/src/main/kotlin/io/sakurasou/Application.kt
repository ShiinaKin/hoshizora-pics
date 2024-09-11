package io.sakurasou

import io.ktor.server.application.*
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val baseUrl = environment.config.property("ktor.application.base-url").getString()

    DatabaseSingleton.init(
        jdbcURL = environment.config.property("ktor.application.database.url").getString(),
        driverClassName = environment.config.property("ktor.application.database.driver").getString(),
        username = environment.config.property("ktor.application.database.username").getString(),
        password = environment.config.property("ktor.application.database.password").getString()
    )

    val redisHost = environment.config.property("ktor.application.cache.redis.host").getString()
    val redisPort = environment.config.property("ktor.application.cache.redis.port").getString()

    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureSwagger(baseUrl)
    configureCache(redisHost, redisPort)
}
