package io.sakurasou.service.common

import at.favre.lib.crypto.bcrypt.BCrypt
import io.sakurasou.config.InstanceCenter
import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.exception.SiteRepeatedInitializationException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.DatabaseSingleton.dbQueryInner
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.SystemStatus
import io.sakurasou.service.album.AlbumService
import io.sakurasou.service.setting.SettingService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/13 15:34
 */
class CommonServiceImpl(
    private val userDao: UserDao,
    private val albumService: AlbumService,
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
            createTime = now,
            updateTime = now
        )

        val oldSiteSetting = settingService.getSiteSetting()
        val siteSettingConfig = SiteSetting(
            siteTitle = siteInitRequest.siteTitle,
            siteSubtitle = siteInitRequest.siteSubtitle,
            siteDescription = siteInitRequest.siteDescription,
            siteKeyword = oldSiteSetting.siteKeyword,
            homePageRandomPicDisplay = oldSiteSetting.homePageRandomPicDisplay
        )

        val systemStatus = SystemStatus(
            isInit = true
        )

        dbQuery {
            val userId = dbQueryInner { userDao.saveUser(userInsertDTO) }
            dbQueryInner { albumService.initAlbumForUser(userId) }
            dbQueryInner { settingService.updateSiteSetting(siteSettingConfig) }
            dbQueryInner { settingService.updateSystemStatus(systemStatus) }
        }
        InstanceCenter.systemStatus = systemStatus
    }
}