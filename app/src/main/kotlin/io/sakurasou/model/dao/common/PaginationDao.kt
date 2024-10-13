package io.sakurasou.model.dao.common

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.controller.param.PagingParameterWrongException
import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.strategy.Strategies
import org.jetbrains.exposed.sql.*

/**
 * @author Shiina Kin
 * 2024/9/23 09:11
 */
interface PaginationDao {
    fun <T> fetchPage(
        table: Table,
        pageRequest: PageRequest,
        customWhereCond: (Query) -> Query = { it -> it },
        transform: (ResultRow) -> T
    ): PageResult<T> {
        val page = pageRequest.page
        val pageSize = pageRequest.pageSize

        val offset = (page - 1) * pageSize

        val totalRecords: Long = customWhereCond(table.selectAll()).count()
        val query = table.selectAll().limit(pageSize).offset(offset)

        pageRequest.orderBy?.let {
            val column = getColumnByName(table, it)
            val sortOrder = SortOrder.valueOf(pageRequest.order?.uppercase() ?: "DESC")
            query.orderBy(column, sortOrder)
        }

        val finalQuery = customWhereCond(query)

        val data = finalQuery.map { transform(it) }.toList()

        val pageResult = PageResult(
            page = page,
            pageSize = pageSize,
            total = totalRecords,
            data = data
        )
        return pageResult
    }

    fun getColumnByName(table: Table, columnName: String): ExpressionWithColumnType<*> {
        return when (table) {
            Images -> Images.columnMap[columnName]
            Albums -> Albums.columnMap[columnName]
            Groups -> Groups.columnMap[columnName]
            Strategies -> Strategies.columnMap[columnName]
            else -> null
        } ?: throw PagingParameterWrongException("Column $columnName not found in table ${table.tableName}")
    }
}
