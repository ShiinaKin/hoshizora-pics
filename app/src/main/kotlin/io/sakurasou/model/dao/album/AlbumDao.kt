package io.sakurasou.model.dao.album

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dao.common.PaginationDao
import io.sakurasou.model.dto.AlbumInsertDTO
import io.sakurasou.model.dto.AlbumUpdateDTO
import io.sakurasou.model.entity.Album

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
interface AlbumDao: PaginationDao {
    fun initAlbumForUser(userId: Long): Long
    fun saveAlbum(insertDTO: AlbumInsertDTO): Long
    fun deleteAlbumById(albumId: Long): Int
    fun updateAlbumById(updateDTO: AlbumUpdateDTO): Int
    fun findAlbumById(albumId: Long): Album?
    fun findDefaultAlbumByUserId(userId: Long): Album
    fun countAlbumByUserId(id: Long): Long
    fun listAlbumByUserId(userId: Long): List<Album>
    fun pagination(userId: Long?, pageRequest: PageRequest): PageResult<AlbumPageVO>
}