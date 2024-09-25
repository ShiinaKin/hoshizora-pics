package io.sakurasou.model.dao.user

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.exception.controller.param.PagingParameterWrongException
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

    override fun pagination(pageRequest: PageRequest): PageResult<UserPageVO> {
        val page = pageRequest.page
        val pageSize = pageRequest.pageSize

        val offset = (page - 1) * pageSize

        val totalRecords: Long = Users.selectAll().count()
        val query = Users
            .join(Groups, JoinType.INNER, Users.groupId, Groups.id)
            .join(Images, JoinType.INNER, Users.id, Images.userId)
            .select(
                Users.id, Users.name, Users.isBanned,
                Groups.name, Images.id.count(), Images.size.sum()
            )
            .groupBy(Users.id, Users.name, Users.isBanned, Groups.name)
            .limit(pageSize, offset)

        val finalQuery = pageRequest.orderBy?.let {
            val sortOrder = SortOrder.valueOf(pageRequest.order?.uppercase() ?: "DESC")
            setPageQueryCondition(query, it, sortOrder)
        } ?: query

        val data = finalQuery.map {
            UserPageVO(
                id = it[Users.id].value,
                username = it[Users.name],
                groupName = it[Groups.name],
                isBanned = it[Users.isBanned],
                imageCount = it[Images.id.count()],
                totalImageSize = it[Images.size.sum()] ?: 0.0
            )
        }.toList()

        val pageResult = PageResult(
            page = page,
            pageSize = pageSize,
            total = totalRecords,
            data = data
        )
        return pageResult
    }

    private val columnMap: Map<String, Column<*>> = mapOf(
        "id" to Users.id,
        "name" to Users.name,
        "isBanned" to Users.isBanned,
        "groupName" to Groups.name,
    )

    private fun setPageQueryCondition(query: Query, columnName: String, order: SortOrder): Query {
        val column = columnMap[columnName] ?: run {
            when (columnName) {
                "imageCount" -> Images.id.count()
                "totalImageSize" -> Images.size.sum()
                else -> throw PagingParameterWrongException("Column $columnName not found in user pagination params")
            }
        }
        return query.orderBy(column, order)
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