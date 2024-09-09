package io.sakurasou.model.dao.strategy

import io.sakurasou.model.strategy.StrategyConfig
import io.sakurasou.plugins.jsonFormat
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 14:03
 */
object Strategies : Table("strategies") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255)
    val config = json<StrategyConfig>("config", jsonFormat)
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    override val primaryKey = PrimaryKey(id)
}