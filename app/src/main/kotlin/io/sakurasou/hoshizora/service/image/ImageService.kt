package io.sakurasou.hoshizora.service.image

import io.sakurasou.hoshizora.controller.request.ImageManagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImageRawFile
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.ImageManagePageVO
import io.sakurasou.hoshizora.controller.vo.ImageManageVO
import io.sakurasou.hoshizora.controller.vo.ImagePageVO
import io.sakurasou.hoshizora.controller.vo.ImageVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.model.dto.ImageFileDTO

/**
 * @author ShiinaKin
 * 2024/9/5 14:42
 */
interface ImageService {
    suspend fun saveImage(
        userId: Long,
        imageRawFile: ImageRawFile,
    ): String

    suspend fun deleteSelfImage(
        userId: Long,
        imageId: Long,
    )

    suspend fun deleteImage(imageId: Long)

    suspend fun patchSelfImage(
        userId: Long,
        imageId: Long,
        selfPatchRequest: ImagePatchRequest,
    )

    suspend fun patchImage(
        imageId: Long,
        managePatchRequest: ImageManagePatchRequest,
    )

    suspend fun fetchSelfImageInfo(
        userId: Long,
        imageId: Long,
    ): ImageVO

    suspend fun fetchImageInfo(imageId: Long): ImageManageVO

    suspend fun fetchSelfImageFile(
        userId: Long,
        imageId: Long,
    ): ImageFileDTO

    suspend fun fetchSelfImageThumbnailFile(
        userId: Long,
        imageId: Long,
    ): ImageFileDTO

    suspend fun fetchImageFile(imageId: Long): ImageFileDTO

    suspend fun fetchImageThumbnailFile(imageId: Long): ImageFileDTO

    suspend fun pageSelfImage(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<ImagePageVO>

    suspend fun pageImage(pageRequest: PageRequest): PageResult<ImageManagePageVO>
}
