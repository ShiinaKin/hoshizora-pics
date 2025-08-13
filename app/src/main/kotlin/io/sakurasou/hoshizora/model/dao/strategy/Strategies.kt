package io.sakurasou.hoshizora.model.dao.strategy

import io.sakurasou.hoshizora.model.strategy.StrategyConfig
import io.sakurasou.hoshizora.plugins.jsonFormat
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 14:03
 */
object Strategies : LongIdTable("strategies") {
    val name = varchar("name", 255).uniqueIndex()
    val isSystemReserved = bool("is_system_reserved")
    val config = json<StrategyConfig>("config", jsonFormat)
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    val columnMap =
        mapOf(
            "createTime" to createTime,
            "updateTime" to updateTime,
        )
}
