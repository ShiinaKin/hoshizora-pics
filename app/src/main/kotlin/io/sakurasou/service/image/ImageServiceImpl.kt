package io.sakurasou.service.image

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.controller.request.ImageManagePatchRequest
import io.sakurasou.controller.request.ImagePatchRequest
import io.sakurasou.controller.request.ImageRawFile
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.*
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.exception.common.FileExtensionNotAllowedException
import io.sakurasou.exception.common.FileSizeException
import io.sakurasou.exception.common.UserBannedException
import io.sakurasou.exception.service.album.AlbumAccessDeniedException
import io.sakurasou.exception.service.album.AlbumNotFoundException
import io.sakurasou.exception.service.group.GroupNotFoundException
import io.sakurasou.exception.service.image.*
import io.sakurasou.exception.service.image.io.ImageThumbnailNotFoundException
import io.sakurasou.exception.service.strategy.StrategyNotFoundException
import io.sakurasou.exception.service.user.UserNotFoundException
import io.sakurasou.execute.executor.image.ImageExecutor
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.ImageFileDTO
import io.sakurasou.model.dto.ImageInsertDTO
import io.sakurasou.model.dto.ImageUpdateDTO
import io.sakurasou.model.entity.Image
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.group.ImageType
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.S3Strategy
import io.sakurasou.service.setting.SettingService
import io.sakurasou.util.ImageUtils
import io.sakurasou.util.PlaceholderUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.apache.commons.codec.digest.DigestUtils
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/**
 * @author ShiinaKin
 * 2024/9/5 15:12
 */
class ImageServiceImpl(
    private val imageDao: ImageDao,
    private val albumDao: AlbumDao,
    private val userDao: UserDao,
    private val groupDao: GroupDao,
    private val strategyDao: StrategyDao,
    private val settingService: SettingService
) : ImageService {
    private val logger = KotlinLogging.logger {}

    override suspend fun saveImage(userId: Long, imageRawFile: ImageRawFile): String {
        val siteSetting = settingService.getSiteSetting()
        return runCatching {
            dbQuery {
                val user = userDao.findUserById(userId) ?: throw UserNotFoundException()
                val group = groupDao.findGroupById(user.groupId) ?: throw GroupNotFoundException()
                val defaultAlbum =
                    albumDao.findAlbumById(user.defaultAlbumId) ?: throw AlbumNotFoundException()
                val strategy = strategyDao.findStrategyById(group.strategyId) ?: throw StrategyNotFoundException()
                val groupConfig = group.config

                // if user is banned
                if (user.isBanned) throw UserBannedException()

                // if over single file maxSize
                if (imageRawFile.size > groupConfig.groupStrategyConfig.singleFileMaxSize) throw FileSizeException()

                // if over group maxSize
                val imageCountAndTotalSizeOfUser = imageDao.getImageCountAndTotalSizeOfUser(user.id)
                val currentUsedSize = imageCountAndTotalSizeOfUser.totalSize
                val maxSize = groupConfig.groupStrategyConfig.maxSize
                if (currentUsedSize + imageRawFile.size > maxSize) throw FileSizeException()

                val fileNamePrefix = imageRawFile.name.split(".")[0]
                var extension = imageRawFile.name.split(".")[1]

                // if extension not allow
                if (!groupConfig.groupStrategyConfig.allowedImageTypes.contains(ImageType.valueOf(extension.uppercase()))) {
                    throw FileExtensionNotAllowedException()
                }

                val fileNamingRule = groupConfig.groupStrategyConfig.fileNamingRule
                val pathNamingRule = groupConfig.groupStrategyConfig.pathNamingRule

                val uniqueName = PlaceholderUtils.parsePlaceholder("{uniq}", "", -1)
                val subFolder = PlaceholderUtils.parsePlaceholder(pathNamingRule, fileNamePrefix, user.id)
                val fileName = PlaceholderUtils.parsePlaceholder(fileNamingRule, fileNamePrefix, user.id)

                // transform image if needed
                var size = imageRawFile.size
                var imageBytes = imageRawFile.bytes
                var image = ByteArrayInputStream(imageBytes).use { ImageIO.read(it) }
                if (groupConfig.groupStrategyConfig.imageAutoTransformTarget != null) {
                    imageBytes = if (groupConfig.groupStrategyConfig.imageQuality != 100) {
                        val quality = groupConfig.groupStrategyConfig.imageQuality
                        if (quality !in (1..100)) throw IllegalArgumentException("Image quality must be in 1..100")
                        val imageQuality = quality / 100.0
                        ImageUtils.transformImage(
                            image,
                            groupConfig.groupStrategyConfig.imageAutoTransformTarget,
                            imageQuality
                        )
                    } else {
                        ImageUtils.transformImage(image, groupConfig.groupStrategyConfig.imageAutoTransformTarget)
                    }
                    extension = groupConfig.groupStrategyConfig.imageAutoTransformTarget.name.lowercase()
                    size = imageBytes.size.toLong()
                    image = ByteArrayInputStream(imageBytes).use { ImageIO.read(it) }
                }
                val md5 = DigestUtils.md5Hex(imageBytes)
                val sha256 = DigestUtils.sha256Hex(imageBytes)

                val storageFileName = "$fileName.$extension"
                val relativePath =
                    ImageUtils.uploadImageAndGetRelativePath(strategy, subFolder, storageFileName, imageBytes)

                val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

                val displayName = "$fileNamePrefix.$extension"
                val imageInsertDTO = ImageInsertDTO(
                    userId = user.id,
                    groupId = group.id,
                    albumId = defaultAlbum.id,
                    uniqueName = uniqueName,
                    displayName = displayName,
                    path = relativePath,
                    strategyId = group.strategyId,
                    originName = imageRawFile.name,
                    mimeType = imageRawFile.mimeType,
                    extension = extension,
                    size = size,
                    width = image.width,
                    height = image.height,
                    md5 = md5,
                    sha256 = sha256,
                    isPrivate = user.isDefaultImagePrivate,
                    createTime = now
                )

                imageDao.saveImage(imageInsertDTO)

                ImageExecutor.persistThumbnail(strategy, subFolder, storageFileName, image)

                if (user.isDefaultImagePrivate) ""
                else "${siteSetting.siteExternalUrl}/s/$uniqueName"
            }
        }.onFailure {
            if (it is ServiceThrowable) throw ImageInsertFailedException(it)
            else throw it
        }.getOrThrow()
    }

    override suspend fun deleteSelfImage(userId: Long, imageId: Long) {
        runCatching {
            dbQuery {
                val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()

                if (image.userId != userId) throw ImageAccessDeniedException()

                val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

                imageDao.deleteImageById(imageId)
                ImageExecutor.deleteImage(strategy, image.path)
                ImageExecutor.deleteThumbnail(strategy, image.path)
            }
        }.onFailure {
            if (it is ServiceThrowable) throw ImageDeleteFailedException(it)
            else throw it
        }
    }

    override suspend fun deleteImage(imageId: Long) {
        runCatching {
            dbQuery {
                val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
                val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

                imageDao.deleteImageById(imageId)
                ImageExecutor.deleteImage(strategy, image.path)
                ImageExecutor.deleteThumbnail(strategy, image.path)
            }
        }.onFailure {
            if (it is ServiceThrowable) throw ImageDeleteFailedException(it)
            else throw it
        }
    }

    override suspend fun patchSelfImage(userId: Long, imageId: Long, selfPatchRequest: ImagePatchRequest) {
        runCatching {
            dbQuery {
                val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
                if (image.userId != userId) throw ImageAccessDeniedException()

                selfPatchRequest.albumId?.let {
                    val album = albumDao.findAlbumById(it) ?: throw AlbumNotFoundException()
                    if (album.userId != userId) throw AlbumAccessDeniedException()
                }

                val imageUpdateDTO = ImageUpdateDTO(
                    id = imageId,
                    albumId = selfPatchRequest.albumId ?: image.albumId,
                    displayName = selfPatchRequest.displayName ?: image.displayName,
                    description = selfPatchRequest.description ?: image.description,
                    isPrivate = selfPatchRequest.isPrivate ?: image.isPrivate
                )

                imageDao.updateImageById(imageUpdateDTO)
            }
        }.onFailure {
            if (it is ServiceThrowable) throw ImageUpdateFailedException(it)
            else throw it
        }
    }

    override suspend fun patchImage(imageId: Long, managePatchRequest: ImageManagePatchRequest) {
        runCatching {
            dbQuery {
                val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()

                if (managePatchRequest.albumId != null) {
                    val album = albumDao.findAlbumById(managePatchRequest.albumId) ?: throw AlbumNotFoundException()
                    if (album.userId != image.userId) throw AlbumAccessDeniedException()
                }

                val imageUpdateDTO = ImageUpdateDTO(
                    id = imageId,
                    albumId = managePatchRequest.albumId ?: image.albumId,
                    displayName = managePatchRequest.displayName ?: image.displayName,
                    description = managePatchRequest.description ?: image.description,
                    isPrivate = managePatchRequest.isPrivate ?: image.isPrivate
                )

                imageDao.updateImageById(imageUpdateDTO)
            }
        }.onFailure {
            if (it is ServiceThrowable) throw ImageUpdateFailedException(it)
            else throw it
        }
    }

    override suspend fun fetchSelfImageInfo(userId: Long, imageId: Long): ImageVO {
        return dbQuery {
            val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
            if (image.userId != userId) throw ImageAccessDeniedException()
            val user = userDao.findUserById(image.userId) ?: throw UserNotFoundException()
            val album = albumDao.findAlbumById(image.albumId) ?: throw AlbumNotFoundException()

            val sizeForVO = image.size / 1024.0 / 1024.0

            ImageVO(
                id = image.id,
                ownerId = user.id,
                ownerName = user.name,
                displayName = image.displayName,
                albumId = album.id,
                albumName = album.name,
                originName = image.originName,
                description = image.description,
                mimeType = image.mimeType,
                size = sizeForVO,
                width = image.width,
                height = image.height,
                md5 = image.md5,
                sha256 = image.sha256,
                isPrivate = image.isPrivate,
                createTime = image.createTime
            )
        }
    }

    override suspend fun fetchImageInfo(imageId: Long): ImageManageVO {
        return dbQuery {
            val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
            val user = userDao.findUserById(image.userId) ?: throw UserNotFoundException()
            val group = groupDao.findGroupById(user.groupId) ?: throw GroupNotFoundException()
            val album = albumDao.findAlbumById(image.albumId) ?: throw AlbumNotFoundException()
            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            val sizeForVO = image.size / 1024.0 / 1024.0

            ImageManageVO(
                id = image.id,
                ownerId = user.id,
                ownerName = user.name,
                groupId = group.id,
                groupName = group.name,
                strategyId = strategy.id,
                strategyName = strategy.name,
                strategyType = strategy.config.strategyType.name,
                displayName = image.displayName,
                albumId = album.id,
                albumName = album.name,
                originName = image.originName,
                description = image.description,
                mimeType = image.mimeType,
                size = sizeForVO,
                width = image.width,
                height = image.height,
                md5 = image.md5,
                sha256 = image.sha256,
                isPrivate = image.isPrivate,
                createTime = image.createTime
            )
        }
    }

    override suspend fun fetchSelfImageFile(userId: Long, imageId: Long): ImageFileDTO {
        return dbQuery {
            val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
            if (image.userId != userId) throw ImageAccessDeniedException()
            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            when (strategy.config) {
                is LocalStrategy -> ImageFileDTO(bytes = ImageUtils.fetchLocalImage(strategy, image.path))
                is S3Strategy -> ImageFileDTO(url = ImageUtils.fetchS3Image(strategy, image.path))
            }
        }
    }

    override suspend fun fetchSelfImageThumbnailFile(userId: Long, imageId: Long): ImageFileDTO {
        return dbQuery {
            val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
            if (image.userId != userId) throw ImageAccessDeniedException()
            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            fetchThumbnailFile(strategy, image)
        }
    }

    override suspend fun fetchImageFile(imageId: Long): ImageFileDTO {
        return dbQuery {
            val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            when (strategy.config) {
                is LocalStrategy -> ImageFileDTO(bytes = ImageUtils.fetchLocalImage(strategy, image.path))
                is S3Strategy -> ImageFileDTO(url = ImageUtils.fetchS3Image(strategy, image.path))
            }
        }
    }

    override suspend fun fetchImageThumbnailFile(imageId: Long): ImageFileDTO {
        return dbQuery {
            val image = imageDao.findImageById(imageId) ?: throw ImageNotFoundException()
            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            fetchThumbnailFile(strategy, image)
        }
    }

    override suspend fun pageSelfImage(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO> {
        val siteExternalUrl = settingService.getSiteSetting().siteExternalUrl
        return dbQuery { imageDao.pagination(userId, pageRequest) }.let {
            it.copy(
                data = it.data.map { image ->
                    ImagePageVO(
                        id = image.id,
                        displayName = image.displayName,
                        isPrivate = image.isPrivate,
                        externalUrl = if (image.isPrivate) "" else "$siteExternalUrl/s/${image.externalUrl}",
                        createTime = image.createTime
                    )
                }
            )
        }
    }

    override suspend fun pageImage(pageRequest: PageRequest): PageResult<ImageManagePageVO> {
        val siteExternalUrl = settingService.getSiteSetting().siteExternalUrl
        return dbQuery { imageDao.paginationForManage(pageRequest) }.let {
            it.copy(
                data = it.data.map { image ->
                    ImageManagePageVO(
                        id = image.id,
                        displayName = image.displayName,
                        userId = image.userId,
                        username = image.username,
                        userEmail = image.userEmail,
                        isPrivate = image.isPrivate,
                        externalUrl = if (image.isPrivate) "" else "$siteExternalUrl/s/${image.externalUrl}",
                        createTime = image.createTime
                    )
                }
            )
        }
    }

    private suspend fun fetchThumbnailFile(strategy: Strategy, image: Image) = when (strategy.config) {
        is LocalStrategy -> ImageFileDTO(bytes = ImageUtils.fetchLocalImage(strategy, image.path, true))
        is S3Strategy -> ImageFileDTO(url = ImageUtils.fetchS3Image(strategy, image.path, true))
    }.also {
        if (it.bytes == null && it.url.isNullOrBlank()) {
            logger.debug { "thumbnail of image ${image.id} doesn't exist, will be generate later." }
            ImageExecutor.rePersistThumbnail(strategy, image.path)
            throw ImageThumbnailNotFoundException()
        }
    }
}