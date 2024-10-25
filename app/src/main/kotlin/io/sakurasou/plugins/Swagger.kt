package io.sakurasou.plugins

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.github.smiley4.schemakenerator.core.handleNameAnnotation
import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.seconds

/**
 * @author Shiina Kin
 * 2024/9/11 13:03
 */

private val logger = KotlinLogging.logger {}

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
        schemas {
            generator = { type ->
                type
                    .processReflection()
                    .handleNameAnnotation()
                    .generateSwaggerSchema()
                    .compileReferencingRoot()
            }
        }
        security {
            defaultSecuritySchemeNames("JWT")
            securityScheme("JWT") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "Bearer"
            }
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

fun generateOpenApiJson() {
    CoroutineScope(Dispatchers.IO).launch {
        delay(5.seconds)
        val client = HttpClient(CIO)
        val response = client.get("http://localhost:8080/api/openapi.json")
        if (response.status != HttpStatusCode.OK) {
            logger.warn { "Failed to generate OpenAPI JSON." }
            return@launch
        }
        val openApiJson = response.bodyAsText()
        val jsonFilePath = "app/openapi/openapi.json"
        Files.deleteIfExists(Path(jsonFilePath))
        FileUtils.writeStringToFile(Path(jsonFilePath).toFile(), openApiJson, Charsets.UTF_8)
        logger.info { "OpenAPI JSON generated successfully." }
    }
}
