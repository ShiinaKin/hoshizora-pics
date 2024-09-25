package io.sakurasou.service.user

import io.sakurasou.controller.request.*
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.controller.vo.UserVO

/**
 * @author ShiinaKin
 * 2024/9/5 15:30
 */
interface UserService {
    suspend fun saveUser(userInsertRequest: UserInsertRequest)
    suspend fun saveUserManually(userManageInsertRequest: UserManageInsertRequest)
    suspend fun deleteUser(id: Long)
    suspend fun patchSelf(id: Long, patchRequest: UserSelfPatchRequest)
    suspend fun patchUser(id: Long, patchRequest: UserManagePatchRequest)
    suspend fun banUser(id: Long)
    suspend fun unbanUser(id: Long)
    suspend fun fetchUser(id: Long): UserVO
    suspend fun pageUsers(pageRequest: PageRequest): PageResult<UserPageVO>
}