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
    val maxSize = double("max_size").default(5 * 1024 * 1024 * 1024.0).check { it greaterEq 0.0 }

    init {
        foreignKey(strategyId to Strategies.id)
    }
}