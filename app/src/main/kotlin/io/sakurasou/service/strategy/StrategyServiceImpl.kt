package io.sakurasou.service.strategy

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.StrategyInsertRequest
import io.sakurasou.controller.request.StrategyPatchRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.StrategyPageVO
import io.sakurasou.controller.vo.StrategyVO
import io.sakurasou.exception.service.strategy.StrategyDeleteFailedException
import io.sakurasou.exception.service.strategy.StrategyInsertFailedException
import io.sakurasou.exception.service.strategy.StrategyNotFoundException
import io.sakurasou.exception.service.strategy.StrategyUpdateFailedException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.dto.StrategyUpdateDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/23 08:05
 */
class StrategyServiceImpl(
    private val strategyDao: StrategyDao
) : StrategyService {
    override suspend fun saveStrategy(insertRequest: StrategyInsertRequest) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val strategyInsertDTO = StrategyInsertDTO(
            name = insertRequest.name,
            config = insertRequest.config,
            createTime = now,
            updateTime = now
        )
        runCatching { dbQuery { strategyDao.saveStrategy(strategyInsertDTO) } }
            .onFailure { throw StrategyInsertFailedException(null, "Possibly due to duplicate StrategyName") }
    }

    override suspend fun deleteStrategy(id: Long) {
        runCatching {
            val influenceRowCnt = dbQuery { strategyDao.deleteStrategyById(id) }
            if (influenceRowCnt < 1) throw StrategyNotFoundException()
        }.onFailure {
            if (it is StrategyNotFoundException) throw StrategyDeleteFailedException(it)
            else throw it
        }
    }

    override suspend fun updateStrategy(id: Long, patchRequest: StrategyPatchRequest) {
        dbQuery {
            val oldStrategy = strategyDao.findStrategyById(id) ?: throw StrategyNotFoundException()
            val strategyUpdateDTO = StrategyUpdateDTO(
                id = id,
                name = patchRequest.name ?: oldStrategy.name,
                config = patchRequest.config ?: oldStrategy.config,
                updateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            )
            runCatching {
                val influenceRowCnt = strategyDao.updateStrategyById(strategyUpdateDTO)
                if (influenceRowCnt < 1) throw StrategyNotFoundException()
            }.onFailure {
                if (it is StrategyNotFoundException) throw StrategyUpdateFailedException(it)
                else throw it
            }
        }
    }

    override suspend fun fetchStrategy(id: Long): StrategyVO {
        val strategy = dbQuery { strategyDao.findStrategyById(id) } ?: throw StrategyNotFoundException()
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
        val pageVOList = strategyPageResult.data.map {
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
            totalPage = strategyPageResult.totalPage,
            data = pageVOList
        )
    }
}