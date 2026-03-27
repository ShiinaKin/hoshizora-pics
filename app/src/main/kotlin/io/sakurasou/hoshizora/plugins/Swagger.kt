package io.sakurasou.hoshizora.plugins

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
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
    routing {
        route("api") {
            swaggerUI("swagger") {
                info =
                    OpenApiInfo(
                        title = "HoshizoraPics API",
                        version = "latest",
                        description = "API for testing and demonstration purposes.",
                    )
                servers {
                    server(baseUrl) {
                        description = "Server"
                    }
                }
                source = OpenApiDocSource.Routing(contentType = ContentType.Application.Json)
                remotePath = "openapi.json"
            }
        }
    }
}

fun generateOpenApiJson() {
    CoroutineScope(Dispatchers.IO).launch {
        delay(5.seconds)
        val client = HttpClient(CIO)
        val response = client.get("http://localhost:8080/api/swagger/openapi.json")
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
