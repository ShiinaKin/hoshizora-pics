package io.sakurasou.model.dao.user

import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.entity.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

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
            it[createTime] = user.createTime
            it[updateTime] = user.updateTime
        }
        return entityID.value
    }

    override fun deleteUserById(id: Long): Int {
        return Users.deleteWhere { Users.id eq id }
    }

    override fun updateUserDefaultAlbumId(userId: Long, defaultAlbumId: Long): Int {
        return Users.update({ Users.id eq userId }) {
            it[Users.defaultAlbumId] = defaultAlbumId
        }
    }

    override fun findUserById(id: Long): User? {
        return Users.selectAll()
            .where { Users.id eq id }
            .map { row ->
                User(
                    row[Users.id].value,
                    row[Users.groupId],
                    row[Users.name],
                    row[Users.password],
                    row[Users.email],
                    row[Users.isDefaultImagePrivate],
                    row[Users.defaultAlbumId]!!,
                    row[Users.createTime],
                    row[Users.updateTime]
                )
            }.firstOrNull()
    }

    override fun findUserByName(name: String): User? {
        return Users.selectAll()
            .where { Users.name eq name }
            .map { row ->
                User(
                    row[Users.id].value,
                    row[Users.groupId],
                    row[Users.name],
                    row[Users.password],
                    row[Users.email],
                    row[Users.isDefaultImagePrivate],
                    row[Users.defaultAlbumId]!!,
                    row[Users.createTime],
                    row[Users.updateTime]
                )
            }.firstOrNull()
    }
}