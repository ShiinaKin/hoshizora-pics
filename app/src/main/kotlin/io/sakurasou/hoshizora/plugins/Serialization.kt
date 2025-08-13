package io.sakurasou.hoshizora.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

val jsonFormat =
    Json {
        encodeDefaults = true
        prettyPrint = false
        isLenient = false
        ignoreUnknownKeys = false
    }
