package io.sakurasou.service.album

import io.sakurasou.controller.request.*
import io.sakurasou.controller.vo.AlbumManagePageVO
import io.sakurasou.controller.vo.AlbumManageVO
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.AlbumVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.service.album.*
import io.sakurasou.exception.service.user.UserNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.dto.AlbumUpdateDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/13 14:48
 */
class AlbumServiceImpl(
    private val userDao: UserDao,
    private val albumDao: AlbumDao,
    private val imageDao: ImageDao
) : AlbumService {
    override suspend fun saveSelf(userId: Long, selfInsertRequest: AlbumSelfInsertRequest) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val albumInsertDTO = AlbumInsertDTO(
            userId = userId,
            name = selfInsertRequest.name,
            description = selfInsertRequest.description,
            imageCount = 0,
            isUncategorized = false,
            createTime = now
        )

        runCatching { dbQuery { albumDao.saveAlbum(albumInsertDTO) } }
            .onFailure { throw AlbumInsertFailedException(null, "Possibly due to duplicate AlbumName") }
    }

    override suspend fun saveAlbum(manageInsertRequest: AlbumManageInsertRequest) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val albumInsertDTO = AlbumInsertDTO(
            userId = manageInsertRequest.userId,
            name = manageInsertRequest.name,
            description = manageInsertRequest.description,
            imageCount = 0,
            isUncategorized = false,
            createTime = now
        )

        runCatching { dbQuery { albumDao.saveAlbum(albumInsertDTO) } }
            .onFailure { throw AlbumInsertFailedException(null, "Possibly due to duplicate AlbumName") }
    }

    override suspend fun deleteSelf(userId: Long, albumId: Long) {
        runCatching {
            dbQuery {
                val album = albumDao.findAlbumById(albumId) ?: throw AlbumNotFoundException()
                if (album.isUncategorized) throw AlbumDeleteFailedException(null, "Cannot delete uncategorized album")
                if (album.userId != userId) throw AlbumAccessDeniedException()

                val uncategorizedAlbum = albumDao.findDefaultAlbumByUserId(album.userId)
                imageDao.updateAlbumIdByAlbumId(albumId, uncategorizedAlbum.id)

                val influenceRowCnt = albumDao.deleteAlbumById(albumId)
                if (influenceRowCnt < 1) throw AlbumDeleteFailedException()
            }
        }.onFailure {
            if (it is AlbumNotFoundException) throw AlbumDeleteFailedException(it)
            else throw it
        }
    }

    override suspend fun deleteAlbum(albumId: Long) {
        runCatching {
            dbQuery {
                val album = albumDao.findAlbumById(albumId) ?: throw AlbumNotFoundException()
                if (album.isUncategorized) throw AlbumDeleteFailedException(null, "Cannot delete uncategorized album")

                val uncategorizedAlbum = albumDao.findDefaultAlbumByUserId(album.userId)
                imageDao.updateAlbumIdByAlbumId(albumId, uncategorizedAlbum.id)

                val influenceRowCnt = albumDao.deleteAlbumById(albumId)
                if (influenceRowCnt < 1) throw AlbumDeleteFailedException()
            }
        }.onFailure {
            if (it is AlbumNotFoundException) throw AlbumDeleteFailedException(it)
            else throw it
        }
    }

    override suspend fun patchSelf(userId: Long, albumId: Long, selfPatchRequest: AlbumSelfPatchRequest) {
        runCatching {
            dbQuery {
                val album = albumDao.findAlbumById(albumId) ?: throw AlbumNotFoundException()
                if (album.userId != userId) throw AlbumAccessDeniedException()

                val albumUpdateDTO = AlbumUpdateDTO(
                    id = albumId,
                    userId = userId,
                    name = selfPatchRequest.name ?: album.name,
                    description = selfPatchRequest.description ?: album.description,
                )

                albumDao.updateAlbumById(albumUpdateDTO)

                // this field is strange to be updated here, cause `defaultAlbumId` is a field of user
                if (selfPatchRequest.isDefault == true) {
                    userDao.updateUserDefaultAlbumId(userId, album.id)
                }
            }
        }.onFailure {
            when (it) {
                is AlbumNotFoundException -> throw AlbumUpdateFailedException(it)
                is AlbumAccessDeniedException -> throw AlbumUpdateFailedException(it)
                else -> throw it
            }
        }
    }

    override suspend fun patchAlbum(albumId: Long, managePatchRequest: AlbumManagePatchRequest) {
        runCatching {
            dbQuery {
                val album = albumDao.findAlbumById(albumId) ?: throw AlbumNotFoundException()

                val albumUpdateDTO = AlbumUpdateDTO(
                    id = albumId,
                    userId = managePatchRequest.userId ?: album.userId,
                    name = managePatchRequest.name ?: album.name,
                    description = managePatchRequest.description ?: album.description,
                )

                albumDao.updateAlbumById(albumUpdateDTO)

                if (managePatchRequest.isDefault == true) {
                    userDao.updateUserDefaultAlbumId(album.userId, album.id)
                }
            }
        }.onFailure {
            when (it) {
                is AlbumNotFoundException -> throw AlbumUpdateFailedException(it)
                else -> throw it
            }
        }
    }

    override suspend fun fetchSelf(userId: Long, albumId: Long): AlbumVO {
        return dbQuery {
            val album = albumDao.findAlbumById(albumId) ?: throw AlbumNotFoundException()
            val user = userDao.findUserById(userId) ?: throw UserNotFoundException()
            if (album.userId != userId) throw AlbumAccessDeniedException()

            val imageCount = imageDao.countImageByAlbumId(albumId)

            AlbumVO(
                id = album.id,
                name = album.name,
                description = album.description,
                imageCount = imageCount,
                isUncategorized = album.isUncategorized,
                isDefault = user.defaultAlbumId == album.id,
                createTime = album.createTime
            )
        }
    }

    override suspend fun fetchAlbum(albumId: Long): AlbumManageVO {
        return dbQuery {
            val album = albumDao.findAlbumById(albumId) ?: throw AlbumNotFoundException()
            val user = userDao.findUserById(album.userId) ?: throw UserNotFoundException()
            val imageCount = imageDao.countImageByAlbumId(albumId)

            AlbumManageVO(
                id = album.id,
                name = album.name,
                userId = user.id,
                username = user.name,
                imageCount = imageCount,
                isUncategorized = album.isUncategorized,
                isDefault = album.id == user.defaultAlbumId,
                createTime = album.createTime
            )
        }
    }

    override suspend fun pageSelf(userId: Long, pageRequest: PageRequest): PageResult<AlbumPageVO> {
        return dbQuery { albumDao.pagination(userId, pageRequest) }
    }

    override suspend fun pageAlbum(pageRequest: PageRequest): PageResult<AlbumManagePageVO> {
        return dbQuery { albumDao.paginationForManage(pageRequest) }
    }
}