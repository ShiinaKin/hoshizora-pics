package io.sakurasou.model.dao.user

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.dto.UserManageUpdateDTO
import io.sakurasou.model.dto.UserSelfUpdateDTO
import io.sakurasou.model.entity.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
class UserDaoImpl : UserDao {
    override fun saveUser(user: UserInsertDTO): Long {
        val entityID = Users.insertAndGetId {
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

    override fun deleteUserById(id: Long): Int {
        return Users.deleteWhere { Users.id eq id }
    }

    override fun updateSelfById(selfUpdateDTO: UserSelfUpdateDTO): Int {
        return Users.update({ Users.id eq selfUpdateDTO.id }) {
            it[password] = selfUpdateDTO.password
            it[email] = selfUpdateDTO.email
            it[isDefaultImagePrivate] = selfUpdateDTO.isDefaultImagePrivate
            it[defaultAlbumId] = selfUpdateDTO.defaultAlbumId
            it[updateTime] = selfUpdateDTO.updateTime
        }
    }

    override fun updateUserById(manageUpdateDTO: UserManageUpdateDTO): Int {
        return Users.update({ Users.id eq manageUpdateDTO.id }) {
            it[groupId] = manageUpdateDTO.groupId
            it[password] = manageUpdateDTO.password
            it[email] = manageUpdateDTO.email
            it[isDefaultImagePrivate] = manageUpdateDTO.isDefaultImagePrivate
            it[defaultAlbumId] = manageUpdateDTO.defaultAlbumId
            it[updateTime] = manageUpdateDTO.updateTime
        }
    }

    override fun updateUserBanStatusById(id: Long, isBan: Boolean): Int {
        return Users.update({ Users.id eq id }) {
            it[isBanned] = isBan
        }
    }

    override fun updateUserDefaultAlbumId(userId: Long, defaultAlbumId: Long): Int {
        return Users.update({ Users.id eq userId }) {
            it[Users.defaultAlbumId] = defaultAlbumId
        }
    }

    override fun findUserById(id: Long): User? {
        return Users.selectAll()
            .where { Users.id eq id }
            .map { row -> toUser(row) }
            .firstOrNull()
    }

    override fun findUserByName(name: String): User? {
        return Users.selectAll()
            .where { Users.name eq name }
            .map { row -> toUser(row) }
            .firstOrNull()
    }

    override fun countUser(): Long {
        return Users.select(Users.id).count()
    }

    override fun pagination(pageRequest: PageRequest): PageResult<UserPageVO> {
        val query = { query: Query ->
            query
                .adjustColumnSet {
                    innerJoin(Groups) { Users.groupId eq Groups.id }
                        .leftJoin(Albums) { Users.id eq Albums.userId }
                        .leftJoin(Images) { Users.id eq Images.userId }
                }
                .adjustSelect {
                    select(
                        Users.fields + Groups.name + Albums.id.count() + Images.id.count()
                                + Coalesce(Images.size.sum(), longLiteral(0))
                    )
                }
                .groupBy(Users.id, Users.name, Users.isBanned, Groups.name, Users.createTime)
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
                isBanned = it[Users.isBanned],
                createTime = it[Users.createTime],
                imageCount = it[Images.id.count()],
                albumCount = it[Albums.id.count()],
                totalImageSize = it[Coalesce(Images.size.sum(), longLiteral(0))].let { size ->
                    if (size != 0L) size / 1024 / 1024.0 else 0.0
                }
            )
        }
    }

    private fun toUser(row: ResultRow) = User(
        id = row[Users.id].value,
        groupId = row[Users.groupId],
        name = row[Users.name],
        password = row[Users.password],
        email = row[Users.email],
        isDefaultImagePrivate = row[Users.isDefaultImagePrivate],
        defaultAlbumId = row[Users.defaultAlbumId]!!,
        isBanned = row[Users.isBanned],
        updateTime = row[Users.updateTime],
        createTime = row[Users.createTime]
    )
}