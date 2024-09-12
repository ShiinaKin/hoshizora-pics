package io.sakurasou.config

import io.ktor.server.application.*
import io.sakurasou.model.DatabaseSingleton

/**
 * @author Shiina Kin
 * 2024/9/12 12:46
 */
fun Application.configureDatabase() {
    DatabaseSingleton.init(
        jdbcURL = environment.config.property("ktor.application.database.url").getString(),
        driverClassName = environment.config.property("ktor.application.database.driver").getString(),
        username = environment.config.property("ktor.application.database.username").getString(),
        password = environment.config.property("ktor.application.database.password").getString()
    )
}