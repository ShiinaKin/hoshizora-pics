package io.sakurasou.hoshizora.service.user

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.UserInsertRequest
import io.sakurasou.hoshizora.controller.request.UserManageInsertRequest
import io.sakurasou.hoshizora.controller.request.UserManagePatchRequest
import io.sakurasou.hoshizora.controller.request.UserSelfPatchRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.UserPageVO
import io.sakurasou.hoshizora.controller.vo.UserVO

/**
 * @author ShiinaKin
 * 2024/9/5 15:30
 */
interface UserService {
    suspend fun saveUser(userInsertRequest: UserInsertRequest)

    suspend fun saveUserManually(userManageInsertRequest: UserManageInsertRequest)

    suspend fun deleteUser(id: Long)

    suspend fun patchSelf(
        id: Long,
        patchRequest: UserSelfPatchRequest,
    )

    suspend fun patchUser(
        id: Long,
        patchRequest: UserManagePatchRequest,
    )

    suspend fun banUser(id: Long)

    suspend fun unbanUser(id: Long)

    suspend fun fetchUser(id: Long): UserVO

    suspend fun pageUsers(pageRequest: PageRequest): PageResult<UserPageVO>
}
