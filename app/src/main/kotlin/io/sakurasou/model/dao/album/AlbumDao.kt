package io.sakurasou.model.dao.album

import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.entity.Album

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
interface AlbumDao {
    fun listAlbumByUserId(userId: Long): List<Album>
    fun getAlbumById(albumId: Long): Album?
    fun saveAlbum(insertDTO: AlbumInsertDTO): Long
}