package io.sakurasou.model.dao.role

import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 13:49
 */
object Roles : Table("roles") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val description = varchar("description", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}