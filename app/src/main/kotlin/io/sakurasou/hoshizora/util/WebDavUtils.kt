package io.sakurasou.hoshizora.util

import io.ktor.client.request.HttpRequestBuilder
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
import io.ktor.util.encodeBase64
import io.sakurasou.hoshizora.di.InstanceCenter.client
import io.sakurasou.hoshizora.exception.service.image.io.webdav.WebDavException
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import kotlinx.coroutines.runBlocking

/**
 * @author ShiinaKin
 * 2025/2/2 11:42
 */
object WebDavUtils {
    fun deleteImage(
        relativePath: String,
        webDavStrategy: WebDavStrategy,
    ) {
        runBlocking {
            client
                .delete("${webDavStrategy.serverUrl}/$relativePath") { addAuthHeader(webDavStrategy) }
                .let {
                    if (it.status != HttpStatusCode.NoContent) {
                        throw WebDavException("Failed to delete image, HTTP status: ${it.status}")
                    }
                }
        }
    }

    fun uploadImage(
        relativePath: String,
        imageBytes: ByteArray,
        webDavStrategy: WebDavStrategy,
    ) {
        runBlocking {
            client
                .put("${webDavStrategy.serverUrl}/$relativePath") {
                    addAuthHeader(webDavStrategy)
                    setBody(imageBytes)
                    contentType(ContentType.Image.Any)
                }.let {
                    if (it.status != HttpStatusCode.Created) throw WebDavException("Failed to upload image, HTTP status: ${it.status}")
                }
        }
    }

    fun fetchImage(
        relativePath: String,
        webDavStrategy: WebDavStrategy,
    ): ByteArray? =
        runBlocking {
            client
                .get("${webDavStrategy.serverUrl}/$relativePath") { addAuthHeader(webDavStrategy) }
                .takeIf { it.status == HttpStatusCode.OK && it.contentType() == ContentType.Image.Any }
                ?.bodyAsBytes()
        }

    fun addThumbnailIdentifierToFileName(fileName: String): String =
        fileName.substringBeforeLast('.') + "_thumbnail" + fileName.substringAfterLast('.')

    private fun HttpRequestBuilder.addAuthHeader(webDavStrategy: WebDavStrategy) {
        headers {
            append(
                HttpHeaders.Authorization,
                "Basic ${"${webDavStrategy.username}:${webDavStrategy.password}".encodeBase64()}",
            )
        }
    }
}
