package io.sakurasou.hoshizora.model.dao.album

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.AlbumManagePageVO
import io.sakurasou.hoshizora.controller.vo.AlbumPageVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.model.dao.common.PaginationDao
import io.sakurasou.hoshizora.model.dto.AlbumInsertDTO
import io.sakurasou.hoshizora.model.dto.AlbumUpdateDTO
import io.sakurasou.hoshizora.model.entity.Album

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
interface AlbumDao : PaginationDao {
    fun initAlbumForUser(userId: Long): Long

    fun saveAlbum(insertDTO: AlbumInsertDTO): Long

    fun deleteAlbumById(albumId: Long): Int

    fun updateAlbumById(updateDTO: AlbumUpdateDTO): Int

    fun findAlbumById(albumId: Long): Album?

    fun findDefaultAlbumByUserId(userId: Long): Album

    fun countAlbum(): Long

    fun countAlbumByUserId(id: Long): Long

    fun listAlbumByUserId(userId: Long): List<Album>

    fun pagination(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<AlbumPageVO>

    fun paginationForManage(pageRequest: PageRequest): PageResult<AlbumManagePageVO>
}
