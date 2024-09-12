package io.sakurasou.model.dao.image

import io.sakurasou.model.dto.ImageInsertDTO
import io.sakurasou.model.entity.Image

/**
 * @author ShiinaKin
 * 2024/9/5 15:33
 */
interface ImageDao {
    fun listImageByAlbumId(albumId: Long): List<Image>
    fun getImageById(imageId: Long): Image?
    fun saveImage(insertDTO: ImageInsertDTO): Long
}