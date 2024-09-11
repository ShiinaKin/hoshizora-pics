package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * @author Shiina Kin
 * 2024/9/11 13:03
 */
fun Application.configureSwagger(baseUrl: String) {
    install(SwaggerUI) {
        info {
            title = "HoshizoraPics API"
            version = "latest"
            description = "API for testing and demonstration purposes."
        }
        server {
            url = baseUrl
            description = "Server"
        }
    }
    routing {
        route("api") {
            route("openapi.json") {
                openApiSpec()
            }
            route("swagger") {
                swaggerUI("/api/openapi.json")
            }
        }
    }
}