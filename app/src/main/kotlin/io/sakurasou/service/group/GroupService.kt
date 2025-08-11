package io.sakurasou.service.group

import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.request.GroupPutRequest
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.GroupPageVO
import io.sakurasou.controller.vo.GroupVO
import io.sakurasou.controller.vo.PageResult

/**
 * @author Shiina Kin
 * 2024/9/13 14:47
 */
interface GroupService {
    suspend fun saveGroup(insertRequest: GroupInsertRequest)

    suspend fun deleteGroup(id: Long)

    suspend fun updateGroup(
        id: Long,
        putRequest: GroupPutRequest,
    )

    suspend fun patchGroup(
        id: Long,
        patchRequest: GroupPatchRequest,
    )

    suspend fun fetchGroup(id: Long): GroupVO

    suspend fun pageGroups(pageRequest: PageRequest): PageResult<GroupPageVO>
}
