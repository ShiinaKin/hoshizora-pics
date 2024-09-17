package io.sakurasou.service.album

import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dto.AlbumInsertDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/13 14:48
 */
class AlbumServiceImpl(
    private val albumDao: AlbumDao
) : AlbumService {
    override suspend fun initAlbumForUser(userId: Long): Long {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val uncategorizedAlbum = AlbumInsertDTO(
            userId = userId,
            name = "uncategorized",
            description = "default, cannot delete",
            imageCount = 0,
            isUncategorized = true,
            createTime = now
        )
        return dbQuery {
            albumDao.saveAlbum(uncategorizedAlbum)
        }
    }

    override suspend fun saveAlbum(): Long {
        TODO("Not yet implemented")
    }
}