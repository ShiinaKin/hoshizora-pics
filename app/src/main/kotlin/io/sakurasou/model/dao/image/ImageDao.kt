package io.sakurasou.model.dao.image

import io.sakurasou.model.dto.ImageCountAndTotalSizeDTO
import io.sakurasou.model.dto.ImageInsertDTO
import io.sakurasou.model.entity.Image

/**
 * @author ShiinaKin
 * 2024/9/5 15:33
 */
interface ImageDao {
    fun saveImage(insertDTO: ImageInsertDTO): Long
    fun deleteImageById(imageId: Long): Int
    fun deleteImageByUserId(userId: Long): Int
    fun updateImageGroupIdByUserId(userId: Long, groupId: Long): Int
    fun updateAlbumIdByAlbumId(oldAlbumId: Long, newAlbumId: Long): Int
    fun getImageCountAndTotalSizeOfUser(userId: Long): ImageCountAndTotalSizeDTO
    fun getImageById(imageId: Long): Image?
    fun countImageByAlbumId(albumId: Long): Long
    fun listImageByAlbumId(albumId: Long): List<Image>
}