package io.sakurasou.hoshizora.model.strategy

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.sakurasou.hoshizora.di.inject
import io.sakurasou.hoshizora.exception.service.image.io.webdav.WebDavException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.getValue
import kotlin.io.encoding.Base64

/**
 * @author Shiina Kin
 * 2025/1/28 14:17
 */
@Serializable
@SerialName("WEBDAV")
@Name("WebDavStrategy")
data class WebDavStrategy(
    val serverUrl: String,
    val username: String,
    val password: String,
    override val uploadFolder: String,
    override val thumbnailFolder: String,
) : StrategyConfig(StrategyType.WEBDAV) {
    companion object {
        fun addThumbnailIdentifierToFileName(fileName: String): String =
            fileName.substringBeforeLast('.') + "_thumbnail" + fileName.substringAfterLast('.')

        private fun addAuthHeader(webDavStrategy: WebDavStrategy) {
            headers {
                append(
                    HttpHeaders.Authorization,
                    "Basic ${Base64.encode("${webDavStrategy.username}:${webDavStrategy.password}".encodeToByteArray())}",
                )
            }
        }
    }

    override suspend fun upload(
        imageBytes: ByteArray,
        uploadPath: String,
    ) {
        val client by inject<HttpClient>()
        client
            .put("$serverUrl/$uploadPath") {
                addAuthHeader(this@WebDavStrategy)
                setBody(imageBytes)
                contentType(ContentType.Image.Any)
            }.let {
                if (it.status != HttpStatusCode.Created) throw WebDavException("Failed to upload image, HTTP status: ${it.status}")
            }
    }

    override suspend fun delete(relativePath: String) {
        val client by inject<HttpClient>()
        client
            .delete("$serverUrl/$relativePath") { addAuthHeader(this@WebDavStrategy) }
            .let {
                if (it.status != HttpStatusCode.NoContent) {
                    throw WebDavException("Failed to delete image, HTTP status: ${it.status}")
                }
            }
    }

    override suspend fun fetch(relativePath: String): ByteArray {
        val client by inject<HttpClient>()
        return client
            .get("$serverUrl/$relativePath") { addAuthHeader(this@WebDavStrategy) }
            .let {
                if (it.status != HttpStatusCode.OK || it.contentType() != ContentType.Image.Any) {
                    throw WebDavException("Failed to fetch image, HTTP status: ${it.status}")
                }
                it.bodyAsBytes()
            }
    }
}
