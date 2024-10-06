package io.sakurasou.service.image

import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.image.ImageDao

/**
 * @author ShiinaKin
 * 2024/9/5 15:12
 */
class ImageServiceImpl(
    private val imageDao: ImageDao,
    private val albumDao: AlbumDao
) : ImageService {

}