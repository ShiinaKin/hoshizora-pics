package io.sakurasou.model.dao.strategy

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.StrategyPageVO
import io.sakurasou.model.dao.common.PaginationDao
import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.dto.StrategyUpdateDTO
import io.sakurasou.model.entity.Strategy

/**
 * @author ShiinaKin
 * 2024/9/7 14:07
 */
interface StrategyDao : PaginationDao {
    fun saveStrategy(insertDTO: StrategyInsertDTO): Long
    fun deleteStrategyById(id: Long): Int
    fun updateStrategyById(updateDTO: StrategyUpdateDTO): Int
    fun findStrategyById(id: Long): Strategy?
    fun pagination(pageRequest: PageRequest): PageResult<StrategyPageVO>
}