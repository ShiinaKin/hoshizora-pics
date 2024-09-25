package io.sakurasou.model.dao.user

import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.entity.User

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
interface UserDao {
    fun saveUser(user: UserInsertDTO): Long
    fun deleteUserById(id: Long): Int
    fun updateUserDefaultAlbumId(userId: Long, defaultAlbumId: Long): Int
    fun findUserById(id: Long): User?
    fun findUserByName(name: String): User?
}