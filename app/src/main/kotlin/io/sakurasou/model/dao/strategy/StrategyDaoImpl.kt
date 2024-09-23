package io.sakurasou.model.dao.strategy

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.dto.StrategyUpdateDTO
import io.sakurasou.model.entity.Strategy
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

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

    override fun deleteStrategyById(id: Long) {
        Strategies.deleteWhere { Strategies.id eq id }
    }

    override fun updateStrategyById(strategy: StrategyUpdateDTO) {
        Strategies.update({ Strategies.id eq strategy.id }) {
            it[name] = strategy.name
            it[config] = strategy.config
            it[updateTime] = strategy.updateTime
        }
    }

    override fun findStrategyById(id: Long): Strategy? {
        return Strategies.selectAll()
            .where(Strategies.id eq id)
            .map {
                Strategy(
                    id = it[Strategies.id].value,
                    name = it[Strategies.name],
                    config = it[Strategies.config],
                    createTime = it[Strategies.createTime],
                    updateTime = it[Strategies.updateTime]
                )
            }.firstOrNull()
    }

    override fun pagination(pageRequest: PageRequest): PageResult<Strategy> {
        return fetchPage(Strategies, pageRequest) { row ->
            Strategy(
                id = row[Strategies.id].value,
                name = row[Strategies.name],
                config = row[Strategies.config],
                createTime = row[Strategies.createTime],
                updateTime = row[Strategies.updateTime]
            )
        }
    }
}