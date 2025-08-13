package io.sakurasou.hoshizora.model.dao.user

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.UserPageVO
import io.sakurasou.hoshizora.model.dao.common.PaginationDao
import io.sakurasou.hoshizora.model.dto.UserInsertDTO
import io.sakurasou.hoshizora.model.dto.UserManageUpdateDTO
import io.sakurasou.hoshizora.model.dto.UserSelfUpdateDTO
import io.sakurasou.hoshizora.model.entity.User

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
interface UserDao : PaginationDao {
    fun saveUser(user: UserInsertDTO): Long

    fun deleteUserById(id: Long): Int

    fun updateSelfById(selfUpdateDTO: UserSelfUpdateDTO): Int

    fun updateUserById(manageUpdateDTO: UserManageUpdateDTO): Int

    fun updateUserBanStatusById(
        id: Long,
        isBan: Boolean,
    ): Int

    fun updateUserDefaultAlbumId(
        userId: Long,
        defaultAlbumId: Long,
    ): Int

    fun findUserById(id: Long): User?

    fun findUserByName(name: String): User?

    fun countUser(): Long

    fun doesUsersBelongToUserGroup(groupId: Long): Boolean

    fun pagination(pageRequest: PageRequest): PageResult<UserPageVO>
}
