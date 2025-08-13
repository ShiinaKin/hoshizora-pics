package io.sakurasou.hoshizora.model.dao.common

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.exception.controller.param.PagingParameterWrongException
import io.sakurasou.hoshizora.model.dao.album.Albums
import io.sakurasou.hoshizora.model.dao.group.Groups
import io.sakurasou.hoshizora.model.dao.image.Images
import io.sakurasou.hoshizora.model.dao.role.Roles
import io.sakurasou.hoshizora.model.dao.strategy.Strategies
import io.sakurasou.hoshizora.model.dao.user.Users
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Shiina Kin
 * 2024/9/23 09:11
 */
interface PaginationDao {
    fun <T> fetchPage(
        table: Table,
        pageRequest: PageRequest,
        customWhereCond: (Query) -> Query = { it },
        transform: (ResultRow) -> T,
    ): PageResult<T> {
        val page = pageRequest.page
        val pageSize = pageRequest.pageSize

        val offset = (page - 1) * pageSize

        val totalRecords: Long = customWhereCond(table.selectAll()).count()
        val totalPage: Long = (totalRecords + pageSize - 1) / pageSize
        val query = table.selectAll().limit(pageSize).offset(offset)

        pageRequest.orderBy?.let {
            val column = getColumnByName(table, it)
            val sortOrder = SortOrder.valueOf(pageRequest.order?.uppercase() ?: "DESC")
            query.orderBy(column, sortOrder)
        }

        val finalQuery = customWhereCond(query)

        val data = finalQuery.map { transform(it) }.toList()

        val pageResult =
            PageResult(
                page = page,
                pageSize = pageSize,
                total = totalRecords,
                totalPage = totalPage,
                data = data,
            )
        return pageResult
    }

    fun getColumnByName(
        table: Table,
        columnName: String,
    ): ExpressionWithColumnType<*> =
        when (table) {
            Images -> Images.columnMap[columnName]
            Albums -> Albums.columnMap[columnName]
            Users -> Users.columnMap[columnName]
            Groups -> Groups.columnMap[columnName]
            Strategies -> Strategies.columnMap[columnName]
            Roles -> Roles.columnMap[columnName]
            else -> null
        } ?: throw PagingParameterWrongException("Column $columnName not found in table ${table.tableName}")
}
