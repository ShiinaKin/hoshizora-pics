package io.sakurasou.model.dao.strategy

import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.entity.Strategy
import org.jetbrains.exposed.sql.insertAndGetId

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
class StrategyDaoImpl : StrategyDao {
    override fun saveStrategy(strategy: StrategyInsertDTO): Long {
        val entityID = Strategies.insertAndGetId {
            it[name] = strategy.name
            it[config] = strategy.config
            it[createTime] = strategy.createTime
            it[updateTime] = strategy.updateTime
        }
        return entityID.value
    }

    override fun findStrategyById(id: Long): Strategy? {
        TODO("Not yet implemented")
    }
}