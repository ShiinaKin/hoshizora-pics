package io.sakurasou.hoshizora.model.dao.role

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 13:49
 */
object Roles : Table("roles") {
    val name = varchar("name", 255)
    val displayName = varchar("display_name", 255)
    val isSystemReserved = bool("is_system_reserved")
    val description = varchar("description", 255).nullable()
    val createTime = datetime("create_time")

    override val primaryKey = PrimaryKey(name)

    val columnMap =
        mapOf(
            "createTime" to createTime,
        )
}
