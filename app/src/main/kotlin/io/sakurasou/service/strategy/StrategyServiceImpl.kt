package io.sakurasou.service.strategy

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.StrategyInsertRequest
import io.sakurasou.controller.request.StrategyPatchRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.StrategyPageVO
import io.sakurasou.controller.vo.StrategyVO
import io.sakurasou.exception.StrategyNotExistException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.dto.StrategyUpdateDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName

/**
 * @author Shiina Kin
 * 2024/9/23 08:05
 */
class StrategyServiceImpl(
    private val strategyDao: StrategyDao
) : StrategyService {
    override suspend fun saveStrategy(insertRequest: StrategyInsertRequest) {
        // val strategyConfig = when (insertRequest.strategyType) {
        //     StrategyType.LOCAL -> Json.decodeFromString<LocalStrategy>(insertRequest.config)
        //     StrategyType.S3 -> Json.decodeFromString<S3Strategy>(insertRequest.config)
        // }
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val strategyInsertDTO = StrategyInsertDTO(
            name = insertRequest.name,
            config = insertRequest.config,
            createTime = now,
            updateTime = now
        )
        dbQuery { strategyDao.saveStrategy(strategyInsertDTO) }
    }

    override suspend fun deleteStrategy(id: Long) {
        dbQuery { strategyDao.deleteStrategyById(id) }
    }

    override suspend fun updateStrategy(id: Long, patchRequest: StrategyPatchRequest) {
        val oldStrategy = fetchStrategy(id)
        val strategyUpdateDTO = StrategyUpdateDTO(
            id = id,
            name = patchRequest.name ?: oldStrategy.name,
            config = patchRequest.config ?: oldStrategy.config,
            updateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
        dbQuery { strategyDao.updateStrategyById(strategyUpdateDTO) }
    }

    override suspend fun fetchStrategy(id: Long): StrategyVO {
        val strategy = dbQuery { strategyDao.findStrategyById(id) } ?: throw StrategyNotExistException()
        val strategyVO = StrategyVO(
            id = strategy.id,
            name = strategy.name,
            config = strategy.config,
            // type = strategy.config::class.annotations.filterIsInstance<SerialName>().firstOrNull()?.value!!,
            type = strategy.config.strategyType,
            createTime = strategy.createTime
        )
        return strategyVO
    }

    override suspend fun pageStrategies(pageRequest: PageRequest): PageResult<StrategyPageVO> {
        val strategyPageResult = dbQuery { strategyDao.pagination(pageRequest) }
        val pageVOList = strategyPageResult.list.map {
            StrategyPageVO(
                id = it.id,
                name = it.name,
                type = it.config.strategyType
            )
        }
        return PageResult(
            page = strategyPageResult.page,
            pageSize = strategyPageResult.pageSize,
            total = strategyPageResult.total,
            list = pageVOList
        )
    }
}