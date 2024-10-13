package io.sakurasou.service.image

import io.sakurasou.controller.request.ImageManagePatchRequest
import io.sakurasou.controller.request.ImagePatchRequest
import io.sakurasou.controller.request.ImageRawFile
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.ImageManageVO
import io.sakurasou.controller.vo.ImagePageVO
import io.sakurasou.controller.vo.ImageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dto.ImageFileDTO

/**
 * @author ShiinaKin
 * 2024/9/5 14:42
 */
interface ImageService {
    suspend fun saveImage(userId: Long, imageRawFile: ImageRawFile): String

    suspend fun deleteSelfImage(userId: Long, imageId: Long)
    suspend fun deleteImage(imageId: Long)

    suspend fun patchSelfImage(userId: Long, imageId: Long, selfPatchRequest: ImagePatchRequest)
    suspend fun patchImage(imageId: Long, managePatchRequest: ImageManagePatchRequest)

    suspend fun fetchSelfImageInfo(userId: Long, imageId: Long): ImageVO
    suspend fun fetchImageInfo(imageId: Long): ImageManageVO

    suspend fun fetchSelfImageFile(userId: Long, imageId: Long): ImageFileDTO
    suspend fun fetchSelfImageThumbnailFile(userId: Long, imageId: Long): ImageFileDTO
    suspend fun fetchImageFile(imageId: Long): ImageFileDTO
    suspend fun fetchImageThumbnailFile(imageId: Long): ImageFileDTO

    suspend fun pageSelfImage(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO>
    suspend fun pageImage(pageRequest: PageRequest): PageResult<ImagePageVO>
}