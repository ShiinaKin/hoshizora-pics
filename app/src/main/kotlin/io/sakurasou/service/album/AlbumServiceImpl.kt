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
    override suspend fun initAlbumForUser(userId: Long) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val uncategorizedAlbum = AlbumInsertDTO(userId, "uncategorized", "default, cannot delete", 0, now)
        dbQuery {
            albumDao.saveAlbum(uncategorizedAlbum)
        }
    }

    override suspend fun saveAlbum() {
        TODO("Not yet implemented")
    }
}