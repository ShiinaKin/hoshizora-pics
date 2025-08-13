package io.sakurasou.hoshizora.model.dao.group

import io.sakurasou.hoshizora.model.dao.image.Images
import io.sakurasou.hoshizora.model.dao.strategy.Strategies
import io.sakurasou.hoshizora.model.dao.user.Users
import io.sakurasou.hoshizora.model.group.GroupConfig
import io.sakurasou.hoshizora.plugins.jsonFormat
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.sum

/**
 * @author ShiinaKin
 * 2024/9/7 13:49
 */
object Groups : LongIdTable("groups") {
    val name = varchar("name", 255).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val strategyId = long("strategy_id")
    val config = json<GroupConfig>("config", jsonFormat)
    val isSystemReserved = bool("is_system_reserved")
    val createTime = datetime("create_time")

    init {
        foreignKey(strategyId to Strategies.id)
    }

    val columnMap =
        mapOf(
            "groupName" to name,
            "userCount" to Users.id.count(),
            "imageCount" to Images.id.count(),
            "imageSize" to Images.size.sum(),
            "createTime" to createTime,
        )
}
