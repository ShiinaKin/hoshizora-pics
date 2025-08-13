package io.sakurasou.hoshizora.model.dao.strategy

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.StrategyPageVO
import io.sakurasou.hoshizora.model.dao.common.PaginationDao
import io.sakurasou.hoshizora.model.dto.StrategyInsertDTO
import io.sakurasou.hoshizora.model.dto.StrategyUpdateDTO
import io.sakurasou.hoshizora.model.entity.Strategy

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
