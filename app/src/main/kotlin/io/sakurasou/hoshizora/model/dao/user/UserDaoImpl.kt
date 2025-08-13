package io.sakurasou.hoshizora.model.dao.user

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.UserPageVO
import io.sakurasou.hoshizora.model.dao.album.Albums
import io.sakurasou.hoshizora.model.dao.group.Groups
import io.sakurasou.hoshizora.model.dao.image.Images
import io.sakurasou.hoshizora.model.dao.user.Users.defaultAlbumId
import io.sakurasou.hoshizora.model.dao.user.Users.email
import io.sakurasou.hoshizora.model.dao.user.Users.isBanned
import io.sakurasou.hoshizora.model.dao.user.Users.isDefaultImagePrivate
import io.sakurasou.hoshizora.model.dao.user.Users.password
import io.sakurasou.hoshizora.model.dto.UserInsertDTO
import io.sakurasou.hoshizora.model.dto.UserManageUpdateDTO
import io.sakurasou.hoshizora.model.dto.UserSelfUpdateDTO
import io.sakurasou.hoshizora.model.entity.User
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.update

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
class UserDaoImpl : UserDao {
    override fun saveUser(user: UserInsertDTO): Long {
        val entityID =
            Users.insertAndGetId {
                it[groupId] = user.groupId
                it[name] = user.username
                it[password] = user.password
                it[email] = user.email
                it[isDefaultImagePrivate] = user.isDefaultImagePrivate
                it[defaultAlbumId] = user.defaultAlbumId
                it[isBanned] = user.isBanned
                it[createTime] = user.createTime
                it[updateTime] = user.updateTime
            }
        return entityID.value
    }

    override fun deleteUserById(id: Long): Int = Users.deleteWhere { Users.id eq id }

    override fun updateSelfById(selfUpdateDTO: UserSelfUpdateDTO): Int =
        Users.update({ Users.id eq selfUpdateDTO.id }) {
            it[password] = selfUpdateDTO.password
            it[email] = selfUpdateDTO.email
            it[isDefaultImagePrivate] = selfUpdateDTO.isDefaultImagePrivate
            it[defaultAlbumId] = selfUpdateDTO.defaultAlbumId
            it[updateTime] = selfUpdateDTO.updateTime
        }

    override fun updateUserById(manageUpdateDTO: UserManageUpdateDTO): Int =
        Users.update({ Users.id eq manageUpdateDTO.id }) {
            it[groupId] = manageUpdateDTO.groupId
            it[password] = manageUpdateDTO.password
            it[email] = manageUpdateDTO.email
            it[isDefaultImagePrivate] = manageUpdateDTO.isDefaultImagePrivate
            it[defaultAlbumId] = manageUpdateDTO.defaultAlbumId
            it[updateTime] = manageUpdateDTO.updateTime
        }

    override fun updateUserBanStatusById(
        id: Long,
        isBan: Boolean,
    ): Int =
        Users.update({ Users.id eq id }) {
            it[isBanned] = isBan
        }

    override fun updateUserDefaultAlbumId(
        userId: Long,
        defaultAlbumId: Long,
    ): Int =
        Users.update({ Users.id eq userId }) {
            it[Users.defaultAlbumId] = defaultAlbumId
        }

    override fun findUserById(id: Long): User? =
        Users
            .selectAll()
            .where { Users.id eq id }
            .map { row -> toUser(row) }
            .firstOrNull()

    override fun findUserByName(name: String): User? =
        Users
            .selectAll()
            .where { Users.name eq name }
            .map { row -> toUser(row) }
            .firstOrNull()

    override fun countUser(): Long = Users.select(Users.id).count()

    override fun doesUsersBelongToUserGroup(groupId: Long): Boolean =
        Users
            .select(Users.groupId)
            .where { Users.groupId eq groupId }
            .count() > 0

    override fun pagination(pageRequest: PageRequest): PageResult<UserPageVO> {
        val query = { query: Query ->
            query
                .adjustColumnSet {
                    innerJoin(Groups) { Users.groupId eq Groups.id }
                        .leftJoin(Albums) { Users.id eq Albums.userId }
                        .leftJoin(Images) { Users.id eq Images.userId }
                }.adjustSelect {
                    select(
                        Users.fields + Groups.name + Albums.id.count() + Images.id.count() +
                            Coalesce(Images.size.sum(), longLiteral(0)),
                    )
                }.groupBy(Users.id, Users.name, isBanned, Groups.name, Users.createTime)
                .also {
                    pageRequest.additionalCondition?.let { map ->
                        map["isBanned"]?.let { isBanned ->
                            it.andWhere { Users.isBanned eq isBanned.toBoolean() }
                        }
                        map["username"]?.let { username ->
                            it.andWhere { Users.name like "%$username%" }
                        }
                    }
                }
        }
        return fetchPage(Users, pageRequest, query) {
            UserPageVO(
                id = it[Users.id].value,
                username = it[Users.name],
                groupName = it[Groups.name],
                isBanned = it[isBanned],
                createTime = it[Users.createTime],
                imageCount = it[Images.id.count()],
                albumCount = it[Albums.id.count()],
                totalImageSize =
                    it[Coalesce(Images.size.sum(), longLiteral(0))].let { size ->
                        if (size != 0L) size / 1024 / 1024.0 else 0.0
                    },
            )
        }
    }

    private fun toUser(row: ResultRow) =
        User(
            id = row[Users.id].value,
            groupId = row[Users.groupId],
            name = row[Users.name],
            password = row[password],
            email = row[email],
            isDefaultImagePrivate = row[isDefaultImagePrivate],
            defaultAlbumId = row[defaultAlbumId]!!,
            isBanned = row[isBanned],
            updateTime = row[Users.updateTime],
            createTime = row[Users.createTime],
        )
}
