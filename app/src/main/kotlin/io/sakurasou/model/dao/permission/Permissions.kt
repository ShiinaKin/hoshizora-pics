package io.sakurasou.model.dao.permission

import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 13:50
 */
object Permissions : Table("permissions") {
    val name = varchar("name", 255)
    val description = varchar("description", 255).nullable()

    override val primaryKey = PrimaryKey(name)
}
