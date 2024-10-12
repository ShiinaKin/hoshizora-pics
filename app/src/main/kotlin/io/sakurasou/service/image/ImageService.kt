package io.sakurasou.service.image

import io.sakurasou.controller.request.ImageManagePatchRequest
import io.sakurasou.controller.request.ImagePatchRequest
import io.sakurasou.controller.request.ImageRawFile
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.ImagePageVO
import io.sakurasou.controller.vo.ImageVO
import io.sakurasou.controller.vo.PageResult

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
    suspend fun fetchSelfImage(userId: Long, imageId: Long): ImageVO
    suspend fun fetchImage(imageId: Long): ImageVO
    suspend fun pageSelfImage(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO>
    suspend fun pageImage(pageRequest: PageRequest): PageResult<ImagePageVO>
}