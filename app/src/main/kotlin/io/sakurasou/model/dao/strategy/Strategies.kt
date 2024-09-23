package io.sakurasou.model.dao.strategy

import io.sakurasou.model.strategy.StrategyConfig
import io.sakurasou.plugins.jsonFormat
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 14:03
 */
object Strategies : LongIdTable("strategies") {
    // Although this will cause an index rebuild when this field is updated,
    // this table won't have a lot of data, so it's fine.
    val name = varchar("name", 255).uniqueIndex()
    val config = json<StrategyConfig>("config", jsonFormat)
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    val columnMap = mapOf(
        "name" to name,
        "createTime" to createTime,
        "updateTime" to updateTime
    )
}