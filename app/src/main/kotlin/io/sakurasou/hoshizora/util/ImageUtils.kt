package io.sakurasou.hoshizora.util

import io.sakurasou.hoshizora.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.group.ImageType
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import org.im4java.process.OutputConsumer
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * @author Shiina Kin
 * 2024/10/12 08:52
 */
object ImageUtils {
    suspend fun uploadImageAndGetRelativePath(
        strategy: Strategy,
        subFolder: String,
        fileName: String,
        imageBytes: ByteArray,
    ): String =
        withContext(Dispatchers.IO) {
            val relativePath = "$subFolder/$fileName"
            val filePath = "${strategy.config.uploadFolder}/$relativePath"
            strategy.config.upload(imageBytes, filePath)
            relativePath
        }

    suspend fun saveThumbnail(
        strategy: Strategy,
        thumbnailBytes: ByteArray,
        relativePath: String,
    ) {
        withContext(Dispatchers.IO) {
            val filePath = "${strategy.config.thumbnailFolder}/$relativePath"
            strategy.config.upload(thumbnailBytes, filePath)
        }
    }

    suspend fun fetchLocalImage(
        strategy: Strategy,
        relativePath: String,
        isThumbnail: Boolean = false,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is S3Strategy -> throw RuntimeException("Not supported")

                is WebDavStrategy -> throw RuntimeException("Not supported")

                is LocalStrategy -> {
                    val parentFolderStr =
                        if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val relativePath = "$parentFolderStr/$relativePath"
                    strategyConfig.fetch(relativePath)
                }
            }
        }

    suspend fun fetchS3Image(
        strategy: Strategy,
        relativePath: String,
        isThumbnail: Boolean = false,
    ): String {
        return withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is LocalStrategy -> throw RuntimeException("Not supported")

                is WebDavStrategy -> throw RuntimeException("Not supported")

                is S3Strategy -> {
                    val folder = if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val s3RelativePath = "$folder/$relativePath"
                    if (!strategyConfig.isImageExist(s3RelativePath)) {
                        if (isThumbnail) return@withContext ""
                        throw ImageFileNotFoundException()
                    }
                    "${strategyConfig.publicUrl}/$s3RelativePath"
                }
            }
        }
    }

    suspend fun fetchWebDavImage(
        strategy: Strategy,
        relativePath: String,
        isThumbnail: Boolean = false,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            when (val strategyConfig = strategy.config) {
                is LocalStrategy -> throw RuntimeException("Not supported")

                is S3Strategy -> throw RuntimeException("Not supported")

                is WebDavStrategy -> {
                    val folder = if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val relativePath = "$folder/$relativePath"
                    val filePath =
                        if (isThumbnail) WebDavStrategy.addThumbnailIdentifierToFileName(relativePath) else relativePath
                    strategyConfig.fetch(filePath)
                }
            }
        }

    suspend fun transformImage(
        rawImage: BufferedImage,
        targetImageType: ImageType,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            val imageOp =
                IMOperation().apply {
                    addImage()
                    addImage("${targetImageType.name}:-")
                }
            execTransform(imageOp, rawImage)
        }

    suspend fun transformImage(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        quality: Double,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            val imageOp =
                IMOperation().apply {
                    addImage()
                    quality(quality)
                    addImage("${targetImageType.name}:-")
                }
            execTransform(imageOp, rawImage)
        }

    private suspend fun transformImage(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newWidth: Int,
        newHeight: Int,
        quality: Double,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            val imageOp =
                IMOperation().apply {
                    addImage()
                    resize(newWidth, newHeight)
                    quality(quality)
                    addImage("${targetImageType.name}:-")
                }
            execTransform(imageOp, rawImage)
        }

    private fun execTransform(
        imageOp: IMOperation,
        rawImage: BufferedImage,
    ): ByteArray =
        ConvertCmd().let { cmd ->
            val outputConverter = CustomOutputConsumer()
            cmd.setOutputConsumer(outputConverter)
            cmd.run(imageOp, rawImage)
            outputConverter.getBytes()
        }

    /**
     * will block cur thread
     */
    fun transformImageByWidth(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newWidth: Int,
        quality: Double,
    ): ByteArray {
        val originalWidth = rawImage.width
        val originalHeight = rawImage.height
        val newHeight = (originalHeight * newWidth) / originalWidth
        return runBlocking { transformImage(rawImage, targetImageType, newWidth, newHeight, quality) }
    }

    /**
     * will block cur thread
     */
    fun transformImageByHeight(
        rawImage: BufferedImage,
        targetImageType: ImageType,
        newHeight: Int,
        quality: Double,
    ): ByteArray {
        val originalWidth = rawImage.width
        val originalHeight = rawImage.height
        val newWidth = (originalWidth * newHeight) / originalHeight
        return runBlocking { transformImage(rawImage, targetImageType, newWidth, newHeight, quality) }
    }

    class CustomOutputConsumer : OutputConsumer {
        private val outputStream = ByteArrayOutputStream()

        override fun consumeOutput(inputStream: InputStream) {
            inputStream.copyTo(outputStream)
            inputStream.close()
        }

        fun getBytes(): ByteArray = outputStream.toByteArray().also { outputStream.close() }
    }
}
