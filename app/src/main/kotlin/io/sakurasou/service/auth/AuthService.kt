package io.sakurasou.service.auth

import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.controller.request.UserLoginRequest

/**
 * @author Shiina Kin
 * 2024/9/12 12:54
 */
interface AuthService {
    suspend fun login(loginRequest: UserLoginRequest): String
    suspend fun saveUser(userInsertRequest: UserInsertRequest)
}