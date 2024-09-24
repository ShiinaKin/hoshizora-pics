package io.sakurasou.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.exception.SignupNotAllowedException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.service.setting.SettingService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author ShiinaKin
 * 2024/9/5 15:30
 */
class UserServiceImpl(
    private val userDao: UserDao,
    private val albumDao: AlbumDao,
    private val settingService: SettingService
) : UserService {
    override suspend fun saveUser(userInsertRequest: UserInsertRequest) {
        val systemSetting = settingService.getSystemSetting()
        if (!systemSetting.allowSignup) throw SignupNotAllowedException()

        val rawPassword = userInsertRequest.password
        val encodePassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())


        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userInsertDTO = UserInsertDTO(
            groupId = systemSetting.defaultGroupId,
            username = userInsertRequest.username,
            password = encodePassword,
            email = userInsertRequest.email,
            isDefaultImagePrivate = true,
            defaultAlbumId = null,
            createTime = now,
            updateTime = now
        )
        dbQuery {
            val userId = userDao.saveUser(userInsertDTO)
            val defaultAlbumId = albumDao.initAlbumForUser(userId)
            userDao.updateUserDefaultAlbumId(userId, defaultAlbumId)
        }
    }

}