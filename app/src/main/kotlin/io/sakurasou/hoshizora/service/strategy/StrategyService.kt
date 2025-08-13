package io.sakurasou.hoshizora.service.strategy

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.StrategyInsertRequest
import io.sakurasou.hoshizora.controller.request.StrategyPatchRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.StrategyPageVO
import io.sakurasou.hoshizora.controller.vo.StrategyVO

/**
 * @author Shiina Kin
 * 2024/9/23 08:04
 */
interface StrategyService {
    suspend fun saveStrategy(insertRequest: StrategyInsertRequest)

    suspend fun deleteStrategy(id: Long)

    suspend fun updateStrategy(
        id: Long,
        patchRequest: StrategyPatchRequest,
    )

    suspend fun fetchStrategy(id: Long): StrategyVO

    suspend fun pageStrategies(pageRequest: PageRequest): PageResult<StrategyPageVO>
}
