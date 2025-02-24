package io.sakurasou.util

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.sakurasou.di.InstanceCenter.client
import io.sakurasou.exception.service.image.io.webdav.WebDavException
import io.sakurasou.model.strategy.WebDavStrategy
import kotlinx.coroutines.runBlocking

/**
 * @author ShiinaKin
 * 2025/2/2 11:42
 */
object WebDavUtils {
    fun deleteImage(relativePath: String, webDavStrategy: WebDavStrategy) {
        runBlocking {
            client.delete("${webDavStrategy.serverUrl}/$relativePath") { addAuthHeader(webDavStrategy) }
                .let {
                    if (it.status != HttpStatusCode.NoContent)
                        throw WebDavException("Failed to delete image, HTTP status: ${it.status}")
                }
        }
    }

    fun uploadImage(relativePath: String, imageBytes: ByteArray, webDavStrategy: WebDavStrategy) {
        runBlocking {
            client.put("${webDavStrategy.serverUrl}/$relativePath") {
                addAuthHeader(webDavStrategy)
                setBody(imageBytes)
                contentType(ContentType.Image.Any)
            }.let {
                if (it.status != HttpStatusCode.Created) throw WebDavException("Failed to upload image, HTTP status: ${it.status}")
            }
        }
    }

    fun fetchImage(relativePath: String, webDavStrategy: WebDavStrategy): ByteArray? {
        return runBlocking {
            client.get("${webDavStrategy.serverUrl}/$relativePath") { addAuthHeader(webDavStrategy) }
                .takeIf { it.status == HttpStatusCode.OK && it.contentType() == ContentType.Image.Any }
                ?.bodyAsBytes()
        }
    }

    fun addThumbnailIdentifierToFileName(fileName: String): String {
        return fileName.substringBeforeLast('.') + "_thumbnail" + fileName.substringAfterLast('.')
    }

    private fun HttpRequestBuilder.addAuthHeader(webDavStrategy: WebDavStrategy) {
        headers {
            append(
                HttpHeaders.Authorization,
                "Basic ${"${webDavStrategy.username}:${webDavStrategy.password}".encodeBase64()}"
            )
        }
    }
}