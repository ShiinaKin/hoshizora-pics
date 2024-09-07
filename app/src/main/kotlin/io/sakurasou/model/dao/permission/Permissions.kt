package io.sakurasou.model.dao.permission

import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 13:50
 */
object Permissions : Table("permissions") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val description = varchar("description", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}