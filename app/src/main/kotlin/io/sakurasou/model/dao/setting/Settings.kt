package io.sakurasou.model.dao.setting

import io.sakurasou.model.setting.SettingConfig
import io.sakurasou.plugins.jsonFormat
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 13:47
 */
object Settings : Table("settings") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255)
    val config = json<SettingConfig>("config", jsonFormat)
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    override val primaryKey = PrimaryKey(id)
}