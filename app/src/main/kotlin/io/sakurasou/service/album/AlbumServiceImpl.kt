package io.sakurasou.service.album

import io.sakurasou.model.dao.album.AlbumDao

/**
 * @author Shiina Kin
 * 2024/9/13 14:48
 */
class AlbumServiceImpl(
    private val albumDao: AlbumDao
) : AlbumService {
    override suspend fun saveAlbum(): Long {
        TODO("Not yet implemented")
    }
}