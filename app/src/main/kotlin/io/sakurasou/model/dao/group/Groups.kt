package io.sakurasou.model.dao.group

import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.strategy.Strategies
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.plugins.jsonFormat
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.json.json
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

    init {
        foreignKey(strategyId to Strategies.id)
    }

    val columnMap = mapOf(
        "name" to name,
        "description" to description,
        "imageCount" to Images.id.count(),
        "size" to Images.size.sum()
    )
}