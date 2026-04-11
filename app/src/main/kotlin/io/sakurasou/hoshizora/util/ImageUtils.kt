package io.sakurasou.hoshizora.util

import io.github.oshai.kotlinlogging.KLogger
import io.sakurasou.hoshizora.exception.service.image.io.ImageFileNotFoundException
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.group.ImageType
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import io.sakurasou.hoshizora.native.ImageBlob
import io.sakurasou.hoshizora.native.ImageOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.foreign.Arena

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
                is S3Strategy -> {
                    throw RuntimeException("Not supported")
                }

                is WebDavStrategy -> {
                    throw RuntimeException("Not supported")
                }

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
                is LocalStrategy -> {
                    throw RuntimeException("Not supported")
                }

                is WebDavStrategy -> {
                    throw RuntimeException("Not supported")
                }

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
                is LocalStrategy -> {
                    throw RuntimeException("Not supported")
                }

                is S3Strategy -> {
                    throw RuntimeException("Not supported")
                }

                is WebDavStrategy -> {
                    val folder = if (isThumbnail) strategyConfig.thumbnailFolder else strategyConfig.uploadFolder
                    val relativePath = "$folder/$relativePath"
                    val filePath =
                        if (isThumbnail) WebDavStrategy.addThumbnailIdentifierToFileName(relativePath) else relativePath
                    strategyConfig.fetch(filePath)
                }
            }
        }

    context(logger: KLogger)
    suspend fun transformImage(
        rawImageBytes: ByteArray,
        targetImageType: ImageType,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            Arena.ofConfined().use { arena ->
                context(arena) {
                    val wand = ImageOperation.newMagickWand()
                    var imageBlob: ImageBlob? = null
                    try {
                        ImageOperation.readImageBlob(wand, rawImageBytes)
                        ImageOperation.setImageFormat(wand, targetImageType)
                        imageBlob = ImageOperation.getImageBlob(wand)
                    } finally {
                        imageBlob?.let { ImageOperation.relinquishMemory(it.blobPtr) }
                        ImageOperation.destroyMagickWand(wand)
                    }
                    imageBlob.bytes
                }
            }
        }

    context(logger: KLogger)
    suspend fun transformImage(
        rawImageBytes: ByteArray,
        targetImageType: ImageType,
        quality: Int,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            Arena.ofConfined().use { arena ->
                context(arena) {
                    val wand = ImageOperation.newMagickWand()
                    var imageBlob: ImageBlob? = null
                    try {
                        ImageOperation.readImageBlob(wand, rawImageBytes)
                        ImageOperation.setImageFormat(wand, targetImageType)
                        ImageOperation.setImageQuality(wand, quality)
                        imageBlob = ImageOperation.getImageBlob(wand)
                    } finally {
                        imageBlob?.let { ImageOperation.relinquishMemory(it.blobPtr) }
                        ImageOperation.destroyMagickWand(wand)
                    }
                    imageBlob.bytes
                }
            }
        }

    context(logger: KLogger)
    private suspend fun transformImage(
        rawImageBytes: ByteArray,
        targetImageType: ImageType,
        targetWidth: Long? = null,
        targetHeight: Long? = null,
        quality: Int,
    ): ByteArray =
        withContext(Dispatchers.IO) {
            Arena.ofConfined().use { arena ->
                context(arena) {
                    val wand = ImageOperation.newMagickWand()
                    var imageBlob: ImageBlob? = null
                    try {
                        ImageOperation.readImageBlob(wand, rawImageBytes)
                        val originalWidth = ImageOperation.getImageWidth(wand)
                        val originalHeight = ImageOperation.getImageHeight(wand)
                        val (newWidth, newHeight) =
                            when {
                                targetWidth != null && targetHeight != null -> {
                                    targetWidth to targetHeight
                                }

                                targetWidth != null -> {
                                    targetWidth to (originalHeight * targetWidth) / originalWidth
                                }

                                targetHeight != null -> {
                                    (originalWidth * targetHeight) / originalHeight to targetHeight
                                }

                                else -> {
                                    originalWidth to originalHeight
                                }
                            }
                        ImageOperation.setImageFormat(wand, targetImageType)
                        ImageOperation.setImageQuality(wand, quality)
                        ImageOperation.resizeImage(wand, newWidth, newHeight)
                        imageBlob = ImageOperation.getImageBlob(wand)
                    } finally {
                        imageBlob?.let { ImageOperation.relinquishMemory(it.blobPtr) }
                        ImageOperation.destroyMagickWand(wand)
                    }
                    imageBlob.bytes
                }
            }
        }

    context(logger: KLogger)
    suspend fun transformImageByWidth(
        rawImageBytes: ByteArray,
        targetImageType: ImageType,
        newWidth: Long,
        quality: Int,
    ): ByteArray =
        transformImage(
            rawImageBytes = rawImageBytes,
            targetImageType = targetImageType,
            targetWidth = newWidth,
            quality = quality,
        )

    context(logger: KLogger)
    suspend fun transformImageByHeight(
        rawImageBytes: ByteArray,
        targetImageType: ImageType,
        newHeight: Long,
        quality: Int,
    ): ByteArray =
        transformImage(
            rawImageBytes = rawImageBytes,
            targetImageType = targetImageType,
            targetHeight = newHeight,
            quality = quality,
        )
}
