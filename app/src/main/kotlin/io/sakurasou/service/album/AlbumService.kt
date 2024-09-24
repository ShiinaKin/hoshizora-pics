package io.sakurasou.service.album

/**
 * @author Shiina Kin
 * 2024/9/13 14:48
 */
interface AlbumService {
    suspend fun saveAlbum(): Long
}