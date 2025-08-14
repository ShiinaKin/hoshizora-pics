package io.sakurasou.hoshizora.model.strategy

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.hoshizora.exception.service.image.io.ImageFileCreateFailedException
import io.sakurasou.hoshizora.exception.service.image.io.ImageFileDeleteFailedException
import io.sakurasou.hoshizora.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.hoshizora.exception.service.image.io.ImageParentFolderCreateFailedException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

/**
 * @author Shiina Kin
 * 2024/9/9 09:26
 */
@Serializable
@SerialName("LOCAL")
@Name("LocalStrategy")
data class LocalStrategy(
    override val uploadFolder: String,
    override val thumbnailFolder: String,
) : StrategyConfig(StrategyType.LOCAL) {
    private val logger = KotlinLogging.logger {}

    override suspend fun upload(
        imageBytes: ByteArray,
        uploadPath: String,
    ) {
        val uploadFile = File(uploadPath)
        val fileName = uploadFile.name
        val parentFolder = uploadFile.parentFile
        if (!parentFolder.exists() && !parentFolder.mkdirs()) {
            throw ImageParentFolderCreateFailedException()
        }
        runCatching {
            uploadFile.writeBytes(imageBytes)
        }.onFailure {
            logger.error(it) { "Failed to save image $fileName at ${uploadFile.absolutePath}" }
            throw ImageFileCreateFailedException()
        }.onSuccess {
            logger.debug { "save image $fileName at ${uploadFile.absolutePath}" }
        }
    }

    override suspend fun delete(relativePath: String) {
        val uploadFile = File(relativePath)

        if (uploadFile.exists()) {
            if (!uploadFile.delete()) {
                logger.error { "Failed to delete image at $uploadFolder/$relativePath" }
                throw ImageFileDeleteFailedException()
            }
        }
    }

    override suspend fun fetch(relativePath: String): ByteArray =
        File(relativePath).takeIf { it.exists() }?.readBytes()
            ?: throw ImageFileNotFoundException()
}
