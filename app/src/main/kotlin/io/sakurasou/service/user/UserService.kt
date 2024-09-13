package io.sakurasou.service.user

import io.sakurasou.controller.request.UserInsertRequest

/**
 * @author ShiinaKin
 * 2024/9/5 15:30
 */
interface UserService {
    suspend fun saveUser(userInsertRequest: UserInsertRequest)
}