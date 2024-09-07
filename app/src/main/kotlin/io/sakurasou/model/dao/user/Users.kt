package io.sakurasou.model.dao.user

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 13:41
 */
object Users : Table("users") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val password = varchar("password", 60)
    val email = varchar("email", 255).nullable()
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    override val primaryKey = PrimaryKey(id)
}