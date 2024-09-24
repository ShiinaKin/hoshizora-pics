package io.sakurasou.model.dao.group

import io.sakurasou.model.dao.strategy.Strategies
import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * @author ShiinaKin
 * 2024/9/7 13:49
 */
object Groups : LongIdTable("groups") {
    val name = varchar("name", 255).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val strategyId = long("strategy_id")
    val maxSize = long("max_size").check { it greaterEq 0 }

    init {
        foreignKey(strategyId to Strategies.id)
    }

    val columnMap = mapOf(
        "name" to name,
        "description" to description,
        "maxSize" to maxSize
    )
}