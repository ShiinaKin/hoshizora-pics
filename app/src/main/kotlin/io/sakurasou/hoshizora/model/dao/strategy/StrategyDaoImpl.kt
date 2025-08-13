package io.sakurasou.hoshizora.model.dao.strategy

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.StrategyPageVO
import io.sakurasou.hoshizora.model.dto.StrategyInsertDTO
import io.sakurasou.hoshizora.model.dto.StrategyUpdateDTO
import io.sakurasou.hoshizora.model.entity.Strategy
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
    override fun saveStrategy(insertDTO: StrategyInsertDTO): Long {
        val entityID =
            Strategies.insertAndGetId {
                it[name] = insertDTO.name
                it[isSystemReserved] = insertDTO.isSystemReserved
                it[config] = insertDTO.config
                it[createTime] = insertDTO.createTime
                it[updateTime] = insertDTO.updateTime
            }
        return entityID.value
    }

    override fun deleteStrategyById(id: Long): Int = Strategies.deleteWhere { Strategies.id eq id }

    override fun updateStrategyById(updateDTO: StrategyUpdateDTO): Int =
        Strategies.update({ Strategies.id eq updateDTO.id }) {
            it[name] = updateDTO.name
            it[config] = updateDTO.config
            it[updateTime] = updateDTO.updateTime
        }

    override fun findStrategyById(id: Long): Strategy? =
        Strategies
            .selectAll()
            .where(Strategies.id eq id)
            .map {
                Strategy(
                    id = it[Strategies.id].value,
                    name = it[Strategies.name],
                    isSystemReserved = it[Strategies.isSystemReserved],
                    config = it[Strategies.config],
                    createTime = it[Strategies.createTime],
                    updateTime = it[Strategies.updateTime],
                )
            }.firstOrNull()

    override fun pagination(pageRequest: PageRequest): PageResult<StrategyPageVO> =
        fetchPage(Strategies, pageRequest) { row ->
            StrategyPageVO(
                id = row[Strategies.id].value,
                name = row[Strategies.name],
                isSystemReserved = row[Strategies.isSystemReserved],
                type = row[Strategies.config].strategyType,
                createTime = row[Strategies.createTime],
            )
        }
}
