package io.sakurasou.model.dao.group

import io.sakurasou.model.dao.strategy.Strategies
import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 13:49
 */
object Groups : Table("groups") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val strategyId = long("strategy_id")
    val maxSize = double("max_size").default(5 * 1024 * 1024 * 1024.0).check { it greaterEq 0.0 }

    override val primaryKey = PrimaryKey(id)

    init {
        foreignKey(strategyId to Strategies.id)
    }
}