package io.sakurasou.service.common

import at.favre.lib.crypto.bcrypt.BCrypt
import io.sakurasou.di.InstanceCenter
import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.controller.vo.CommonSiteSetting
import io.sakurasou.exception.controller.access.RandomFetchAllowedException
import io.sakurasou.exception.controller.status.SiteRepeatedInitializationException
import io.sakurasou.exception.service.image.ImageAccessDeniedException
import io.sakurasou.exception.service.image.ImageNotFoundException
import io.sakurasou.exception.service.strategy.StrategyNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.ImageFileDTO
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.SystemStatus
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.S3Strategy
import io.sakurasou.service.setting.SettingService
import io.sakurasou.util.ImageUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/13 15:34
 */
class CommonServiceImpl(
    private val userDao: UserDao,
    private val albumDao: AlbumDao,
    private val strategyDao: StrategyDao,
    private val imageDao: ImageDao,
    private val settingService: SettingService
) : CommonService {
    override suspend fun initSite(siteInitRequest: SiteInitRequest) {
        val status = settingService.getSystemStatus()
        if (status.isInit) throw SiteRepeatedInitializationException()

        val rawPassword = siteInitRequest.password
        val encodePassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userInsertDTO = UserInsertDTO(
            groupId = 1,
            username = siteInitRequest.username,
            password = encodePassword,
            email = siteInitRequest.email,
            isDefaultImagePrivate = true,
            defaultAlbumId = null,
            isBanned = false,
            createTime = now,
            updateTime = now
        )

        val oldSiteSetting = settingService.getSiteSetting()
        val siteSettingConfig = SiteSetting(
            siteExternalUrl = siteInitRequest.siteExternalUrl,
            siteTitle = siteInitRequest.siteTitle,
            siteSubtitle = siteInitRequest.siteSubtitle,
            siteDescription = siteInitRequest.siteDescription
        )

        val systemStatus = SystemStatus(
            isInit = true,
            version = status.version
        )

        dbQuery {
            val userId = userDao.saveUser(userInsertDTO)
            val defaultAlbumId = albumDao.initAlbumForUser(userId)
            userDao.updateUserDefaultAlbumId(userId, defaultAlbumId)
            settingService.updateSiteSetting(siteSettingConfig)
            settingService.updateSystemStatus(systemStatus)
        }
        InstanceCenter.systemStatus = systemStatus
    }

    override suspend fun fetchCommonSiteSetting(): CommonSiteSetting {
        val systemStatus = settingService.getSystemStatus()
        val siteSetting = settingService.getSiteSetting()
        val systemSetting = settingService.getSystemSetting()
        return CommonSiteSetting(
            isSiteInit = systemStatus.isInit,
            siteTitle = siteSetting.siteTitle,
            siteSubTitle = siteSetting.siteSubtitle,
            siteDescription = siteSetting.siteDescription,
            siteAllowSignup = systemSetting.allowSignup,
        )
    }

    override suspend fun fetchImage(imageUniqueName: String): ImageFileDTO {
        return dbQuery {
            val image = imageDao.findImageByUniqueName(imageUniqueName) ?: throw ImageNotFoundException()
            if (image.isPrivate) throw ImageAccessDeniedException()

            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            when(strategy.config) {
                is LocalStrategy -> ImageFileDTO(bytes = ImageUtils.fetchLocalImage(strategy, image.path))
                is S3Strategy -> ImageFileDTO(url = ImageUtils.fetchS3Image(strategy, image.path))
            }
        }
    }

    override suspend fun fetchRandomImage(): ImageFileDTO {
        if (!settingService.getSystemSetting().allowRandomFetch) throw RandomFetchAllowedException()
        return dbQuery {

            val image = imageDao.findRandomImage() ?: throw ImageNotFoundException()
            val strategy = strategyDao.findStrategyById(image.strategyId) ?: throw StrategyNotFoundException()

            when(strategy.config) {
                is LocalStrategy -> ImageFileDTO(bytes = ImageUtils.fetchLocalImage(strategy, image.path))
                is S3Strategy -> ImageFileDTO(url = ImageUtils.fetchS3Image(strategy, image.path))
            }
        }
    }
}