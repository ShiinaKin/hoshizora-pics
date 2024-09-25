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
    fun updateImageGroupIdByUserId(userId: Long, groupId: Long): Int
    fun listImageByAlbumId(albumId: Long): List<Image>
    fun getImageCountAndTotalSizeOfUser(userId: Long): ImageCountAndTotalSizeDTO
    fun getImageById(imageId: Long): Image?
}