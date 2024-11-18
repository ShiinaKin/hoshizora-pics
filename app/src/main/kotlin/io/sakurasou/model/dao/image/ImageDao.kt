package io.sakurasou.model.dao.image

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.ImageManagePageVO
import io.sakurasou.controller.vo.ImagePageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dao.common.PaginationDao
import io.sakurasou.model.dto.ImageCountAndTotalSizeDTO
import io.sakurasou.model.dto.ImageInsertDTO
import io.sakurasou.model.dto.ImageUpdateDTO
import io.sakurasou.model.entity.Image

/**
 * @author ShiinaKin
 * 2024/9/5 15:33
 */
interface ImageDao : PaginationDao {
    fun saveImage(insertDTO: ImageInsertDTO): Long

    fun deleteImageById(imageId: Long): Int
    fun deleteImageByUserId(userId: Long): Int

    fun updateImageById(imageUpdateDTO: ImageUpdateDTO): Int
    fun updateImageGroupIdByUserId(userId: Long, groupId: Long): Int
    fun updateAlbumIdByAlbumId(oldAlbumId: Long, newAlbumId: Long): Int

    fun getImageCountAndTotalSize(): ImageCountAndTotalSizeDTO
    fun getImageCountAndTotalSizeOfUser(userId: Long): ImageCountAndTotalSizeDTO
    fun findImageById(imageId: Long): Image?
    fun findImageByUniqueName(imageUniqueName: String): Image?
    fun findRandomImage(): Image?

    fun countImageByAlbumId(albumId: Long): Long
    fun listImageByAlbumId(albumId: Long): List<Image>
    fun pagination(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO>
    fun paginationForManage(pageRequest: PageRequest): PageResult<ImageManagePageVO>
}