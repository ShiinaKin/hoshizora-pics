package io.sakurasou.model.dao.user

import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.entity.User

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
interface UserDao {
    fun findUserById(id: Long): User?
    fun findUserByName(name: String): User?
    fun saveUser(user: UserInsertDTO): Long
}