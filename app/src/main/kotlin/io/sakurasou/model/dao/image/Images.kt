package io.sakurasou.model.dao.image

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/5 14:52
 */
object Images: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 127)
    val url = varchar("url", length = 255)
    val size = integer("size")
    val type = varchar("type", length = 50)
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    override val primaryKey = PrimaryKey(id)
}