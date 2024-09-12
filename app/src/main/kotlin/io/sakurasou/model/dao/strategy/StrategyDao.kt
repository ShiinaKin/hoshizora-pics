package io.sakurasou.model.dao.strategy

import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.entity.Strategy

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
interface StrategyDao {
    fun saveStrategy(strategy: StrategyInsertDTO): Long
    fun findStrategyById(id: Long): Strategy?
}