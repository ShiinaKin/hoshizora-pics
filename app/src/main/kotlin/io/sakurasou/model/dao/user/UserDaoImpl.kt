package io.sakurasou.model.dao.user

import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.entity.User
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

/**
 * @author ShiinaKin
 * 2024/9/7 14:06
 */
class UserDaoImpl : UserDao {
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
                    row[Users.createTime],
                    row[Users.updateTime]
                )
            }.firstOrNull()
    }

    override fun saveUser(user: UserInsertDTO): Long {
        val entityID = Users.insertAndGetId {
            it[groupId] = user.groupId
            it[name] = user.username
            it[password] = user.password
            it[email] = user.email
            it[createTime] = user.createTime
            it[updateTime] = user.updateTime
        }
        return entityID.value
    }


}