package io.sakurasou.service.strategy

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.StrategyInsertRequest
import io.sakurasou.controller.request.StrategyPatchRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.StrategyPageVO
import io.sakurasou.controller.vo.StrategyVO

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
