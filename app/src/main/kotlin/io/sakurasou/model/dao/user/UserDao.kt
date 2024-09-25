package io.sakurasou.model.dao.user

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.dto.UserManageUpdateDTO
import io.sakurasou.model.dto.UserSelfUpdateDTO
import io.sakurasou.model.entity.User

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
interface UserDao {
    fun saveUser(user: UserInsertDTO): Long
    fun deleteUserById(id: Long): Int
    fun updateSelfById(selfUpdateDTO: UserSelfUpdateDTO): Int
    fun updateUserById(manageUpdateDTO: UserManageUpdateDTO): Int
    fun updateUserBanStatusById(id: Long, isBan: Boolean): Int
    fun updateUserDefaultAlbumId(userId: Long, defaultAlbumId: Long): Int
    fun findUserById(id: Long): User?
    fun findUserByName(name: String): User?
    fun pagination(pageRequest: PageRequest): PageResult<UserPageVO>
}