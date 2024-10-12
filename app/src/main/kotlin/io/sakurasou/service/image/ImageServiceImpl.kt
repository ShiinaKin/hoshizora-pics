package io.sakurasou.service.image

import io.sakurasou.controller.request.ImageManagePatchRequest
import io.sakurasou.controller.request.ImagePatchRequest
import io.sakurasou.controller.request.ImageRawFile
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.ImagePageVO
import io.sakurasou.controller.vo.ImageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.common.FileExtensionNotAllowedException
import io.sakurasou.exception.common.FileSizeException
import io.sakurasou.exception.common.UserBannedException
import io.sakurasou.exception.service.strategy.StrategyNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.ImageInsertDTO
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
    override suspend fun saveImage(userId: Long, imageRawFile: ImageRawFile): String {
        val siteSetting = settingService.getSiteSetting()
        return dbQuery {
            val user = userDao.findUserById(userId) ?: throw IllegalArgumentException("User not found")
            val group = groupDao.findGroupById(user.groupId) ?: throw IllegalArgumentException("Group not found")
            val defaultAlbum =
                albumDao.findAlbumById(user.defaultAlbumId) ?: throw IllegalArgumentException("Default Album not found")
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

            val name = PlaceholderUtils.parsePlaceholder(fileNamingRule, fileNamePrefix, user.id)
            val subFolder = PlaceholderUtils.parsePlaceholder(pathNamingRule, fileNamePrefix, user.id)

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

            val fileName = "$name.$extension"
            val relativePath = ImageUtils.uploadImageAndGetRelativePath(
                strategy = strategy,
                subFolder = subFolder,
                fileName = fileName,
                bytes = imageBytes
            )

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            val imageInsertDTO = ImageInsertDTO(
                userId = user.id,
                groupId = group.id,
                albumId = defaultAlbum.id,
                name = name,
                path = relativePath,
                strategyId = group.strategyId,
                originName = "$fileNamePrefix.$extension",
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

            if (user.isDefaultImagePrivate) ""
            else when (strategy.config) {
                is LocalStrategy -> "${siteSetting.siteExternalUrl}/$relativePath"
                is S3Strategy -> "${strategy.config.publicUrl}/$relativePath"
            }
        }
    }

    override suspend fun deleteSelfImage(userId: Long, imageId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteImage(imageId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun patchSelfImage(userId: Long, imageId: Long, selfPatchRequest: ImagePatchRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun patchImage(imageId: Long, managePatchRequest: ImageManagePatchRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchSelfImage(userId: Long, imageId: Long): ImageVO {
        TODO("Not yet implemented")
    }

    override suspend fun fetchImage(imageId: Long): ImageVO {
        TODO("Not yet implemented")
    }

    override suspend fun pageSelfImage(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO> {
        TODO("Not yet implemented")
    }

    override suspend fun pageImage(pageRequest: PageRequest): PageResult<ImagePageVO> {
        TODO("Not yet implemented")
    }
}