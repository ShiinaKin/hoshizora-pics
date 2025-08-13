package io.sakurasou.hoshizora.service.group

import io.sakurasou.hoshizora.controller.request.GroupInsertRequest
import io.sakurasou.hoshizora.controller.request.GroupPatchRequest
import io.sakurasou.hoshizora.controller.request.GroupPutRequest
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.GroupPageVO
import io.sakurasou.hoshizora.controller.vo.GroupVO
import io.sakurasou.hoshizora.controller.vo.PageResult

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
