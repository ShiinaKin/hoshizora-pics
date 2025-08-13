package io.sakurasou.hoshizora.service.album

import io.sakurasou.hoshizora.controller.request.AlbumManageInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumManagePatchRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfPatchRequest
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.AlbumManagePageVO
import io.sakurasou.hoshizora.controller.vo.AlbumManageVO
import io.sakurasou.hoshizora.controller.vo.AlbumPageVO
import io.sakurasou.hoshizora.controller.vo.AlbumVO
import io.sakurasou.hoshizora.controller.vo.PageResult

/**
 * @author Shiina Kin
 * 2024/9/13 14:48
 */
interface AlbumService {
    suspend fun saveSelf(
        userId: Long,
        selfInsertRequest: AlbumSelfInsertRequest,
    )

    suspend fun saveAlbum(manageInsertRequest: AlbumManageInsertRequest)

    suspend fun deleteSelf(
        userId: Long,
        albumId: Long,
    )

    suspend fun deleteAlbum(albumId: Long)

    suspend fun patchSelf(
        userId: Long,
        albumId: Long,
        selfPatchRequest: AlbumSelfPatchRequest,
    )

    suspend fun patchAlbum(
        albumId: Long,
        managePatchRequest: AlbumManagePatchRequest,
    )

    suspend fun fetchSelf(
        userId: Long,
        albumId: Long,
    ): AlbumVO

    suspend fun fetchAlbum(albumId: Long): AlbumManageVO

    suspend fun pageSelf(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<AlbumPageVO>

    suspend fun pageAlbum(pageRequest: PageRequest): PageResult<AlbumManagePageVO>
}
