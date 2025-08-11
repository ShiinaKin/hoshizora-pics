package io.sakurasou.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import io.sakurasou.controller.request.*
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.controller.vo.UserVO
import io.sakurasou.exception.controller.access.SignupNotAllowedException
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.exception.service.album.AlbumNotFoundException
import io.sakurasou.exception.service.user.UserDeleteFailedException
import io.sakurasou.exception.service.user.UserInsertFailedException
import io.sakurasou.exception.service.user.UserNotFoundException
import io.sakurasou.exception.service.user.UserUpdateFailedException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.dto.UserManageUpdateDTO
import io.sakurasou.model.dto.UserSelfUpdateDTO
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
    private val groupDao: GroupDao,
    private val albumDao: AlbumDao,
    private val imageDao: ImageDao,
    private val settingService: SettingService,
) : UserService {
    override suspend fun saveUser(userInsertRequest: UserInsertRequest) {
        val systemSetting = settingService.getSystemSetting()
        if (!systemSetting.allowSignup) throw SignupNotAllowedException()

        val rawPassword = userInsertRequest.password
        val encodePassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val userInsertDTO =
            UserInsertDTO(
                groupId = systemSetting.defaultGroupId,
                username = userInsertRequest.username,
                password = encodePassword,
                email = userInsertRequest.email,
                isDefaultImagePrivate = true,
                defaultAlbumId = null,
                isBanned = false,
                updateTime = now,
                createTime = now,
            )
        dbQuery {
            val userId = userDao.saveUser(userInsertDTO)
            val defaultAlbumId = albumDao.initAlbumForUser(userId)
            userDao.updateUserDefaultAlbumId(userId, defaultAlbumId)
        }
    }

    override suspend fun saveUserManually(userManageInsertRequest: UserManageInsertRequest) {
        val rawPassword = userManageInsertRequest.password
        val encodePassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray())

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val userInsertDTO =
            UserInsertDTO(
                groupId = userManageInsertRequest.groupId,
                username = userManageInsertRequest.username,
                password = encodePassword,
                email = userManageInsertRequest.email,
                isDefaultImagePrivate = userManageInsertRequest.isDefaultImagePrivate,
                defaultAlbumId = null,
                isBanned = false,
                updateTime = now,
                createTime = now,
            )
        runCatching {
            dbQuery {
                val userId = userDao.saveUser(userInsertDTO)
                val defaultAlbumId = albumDao.initAlbumForUser(userId)
                userDao.updateUserDefaultAlbumId(userId, defaultAlbumId)
                Unit
            }
        }.onFailure {
            throw UserInsertFailedException(null, "Possibly due to duplicate Username")
        }
    }

    override suspend fun deleteUser(id: Long) {
        runCatching {
            dbQuery {
                imageDao.deleteImageByUserId(id)
                val influenceRowCnt = userDao.deleteUserById(id)
                if (influenceRowCnt < 1) throw UserNotFoundException()
            }
        }.onFailure {
            if (it is UserNotFoundException) {
                throw UserDeleteFailedException(it)
            } else {
                throw it
            }
        }
    }

    override suspend fun patchSelf(
        id: Long,
        patchRequest: UserSelfPatchRequest,
    ) {
        dbQuery {
            val oldUserInfo = userDao.findUserById(id) ?: throw UserNotFoundException()

            val encodePassword =
                patchRequest.password?.let {
                    BCrypt.withDefaults().hashToString(12, it.toCharArray())
                } ?: oldUserInfo.password
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            val selfUpdateDTO =
                UserSelfUpdateDTO(
                    id = id,
                    password = encodePassword,
                    email = patchRequest.email ?: oldUserInfo.email,
                    isDefaultImagePrivate = patchRequest.isDefaultImagePrivate ?: oldUserInfo.isDefaultImagePrivate,
                    defaultAlbumId = patchRequest.defaultAlbumId ?: oldUserInfo.defaultAlbumId,
                    updateTime = now,
                )

            val isModifyDefaultAlbum = patchRequest.defaultAlbumId != null

            runCatching {
                if (isModifyDefaultAlbum) {
                    val album = albumDao.findAlbumById(selfUpdateDTO.defaultAlbumId!!) ?: throw AlbumNotFoundException()
                    if (album.userId != id) throw WrongParameterException("Album not belong to user")
                }
                val influenceRowCnt = userDao.updateSelfById(selfUpdateDTO)
                if (influenceRowCnt < 1) throw UserNotFoundException()
            }.onFailure {
                if (it is UserNotFoundException) {
                    throw UserUpdateFailedException(it)
                } else {
                    throw it
                }
            }
        }
    }

    override suspend fun patchUser(
        id: Long,
        patchRequest: UserManagePatchRequest,
    ) {
        dbQuery {
            val oldUserInfo = userDao.findUserById(id) ?: throw UserNotFoundException()

            val encodePassword =
                patchRequest.password?.let {
                    BCrypt.withDefaults().hashToString(12, it.toCharArray())
                } ?: oldUserInfo.password
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

            val userUpdateDTO =
                UserManageUpdateDTO(
                    id = id,
                    groupId = patchRequest.groupId ?: oldUserInfo.groupId,
                    password = encodePassword,
                    email = patchRequest.email ?: oldUserInfo.email,
                    isDefaultImagePrivate = patchRequest.isDefaultImagePrivate ?: oldUserInfo.isDefaultImagePrivate,
                    defaultAlbumId = patchRequest.defaultAlbumId ?: oldUserInfo.defaultAlbumId,
                    updateTime = now,
                )

            val isModifyGroup = patchRequest.groupId != null
            val isModifyDefaultAlbum = patchRequest.defaultAlbumId != null

            runCatching {
                if (isModifyDefaultAlbum) {
                    val album = albumDao.findAlbumById(userUpdateDTO.defaultAlbumId!!) ?: throw AlbumNotFoundException()
                    if (album.userId != id) throw WrongParameterException("Album not belong to user")
                }
                val influenceRowCnt = userDao.updateUserById(userUpdateDTO)
                if (influenceRowCnt < 1) throw UserNotFoundException()
                if (isModifyGroup) imageDao.updateImageGroupIdByUserId(id, userUpdateDTO.groupId)
            }.onFailure {
                if (it is UserNotFoundException) {
                    throw UserUpdateFailedException(it)
                } else {
                    throw it
                }
            }
        }
    }

    override suspend fun banUser(id: Long) {
        runCatching {
            dbQuery {
                val influenceRowCnt = userDao.updateUserBanStatusById(id, true)
                if (influenceRowCnt < 1) throw UserNotFoundException()
            }
        }.onFailure {
            if (it is UserNotFoundException) {
                throw UserUpdateFailedException(it)
            } else {
                throw it
            }
        }
    }

    override suspend fun unbanUser(id: Long) {
        runCatching {
            dbQuery {
                val influenceRowCnt = userDao.updateUserBanStatusById(id, false)
                if (influenceRowCnt < 1) throw UserNotFoundException()
            }
        }.onFailure {
            if (it is UserNotFoundException) {
                throw UserUpdateFailedException(it)
            } else {
                throw it
            }
        }
    }

    override suspend fun fetchUser(id: Long): UserVO =
        dbQuery {
            val user = userDao.findUserById(id) ?: throw UserNotFoundException()
            val group = groupDao.findGroupById(user.groupId)!!
            val albumCount = albumDao.countAlbumByUserId(id)
            val (count, totalSize) = imageDao.getImageCountAndTotalSizeOfUser(id)
            UserVO(
                id = user.id,
                username = user.name,
                groupId = group.id,
                groupName = group.name,
                email = user.email,
                isDefaultImagePrivate = user.isDefaultImagePrivate,
                isBanned = user.isBanned,
                createTime = user.createTime,
                imageCount = count,
                albumCount = albumCount,
                totalImageSize = totalSize / 1024 / 1024.0,
                allSize = group.config.groupStrategyConfig.maxSize / 1024 / 1024.0,
            )
        }

    override suspend fun pageUsers(pageRequest: PageRequest): PageResult<UserPageVO> = dbQuery { userDao.pagination(pageRequest) }
}
