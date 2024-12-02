package io.sakurasou.service.group

import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.GroupPageVO
import io.sakurasou.controller.vo.GroupVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.service.group.GroupDeleteFailedException
import io.sakurasou.exception.service.group.GroupInsertFailedException
import io.sakurasou.exception.service.group.GroupNotFoundException
import io.sakurasou.exception.service.group.GroupUpdateFailedException
import io.sakurasou.exception.service.role.RoleNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.model.group.GroupStrategyConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/24 12:41
 */
class GroupServiceImpl(
    private val groupDao: GroupDao,
    private val strategyDao: StrategyDao,
    private val relationDao: RelationDao
) : GroupService {
    override suspend fun saveGroup(insertRequest: GroupInsertRequest) {
        val groupInsertDTO = GroupInsertDTO(
            name = insertRequest.name,
            description = insertRequest.description,
            strategyId = insertRequest.strategyId,
            config = insertRequest.config,
            isSystemReserved = false,
            createTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
        val groupRoles = insertRequest.roles

        runCatching {
            dbQuery {
                val groupId = groupDao.saveGroup(groupInsertDTO)
                runCatching { relationDao.batchInsertGroupToRoles(groupId, groupRoles) }
                    .onFailure { throw RoleNotFoundException() }
                Unit
            }
        }.onFailure {
            if (it is RoleNotFoundException) throw GroupInsertFailedException(it)
            else throw GroupInsertFailedException(null, "Possibly due to duplicate GroupName")
        }
    }

    override suspend fun deleteGroup(id: Long) {
        runCatching {
            val influenceRow = dbQuery { groupDao.deleteGroupById(id) }
            if (influenceRow < 1) throw GroupNotFoundException()
        }.onFailure {
            if (it is GroupNotFoundException) throw GroupDeleteFailedException(it)
            else throw it
        }
    }

    override suspend fun updateGroup(id: Long, patchRequest: GroupPatchRequest) {
        dbQuery {
            runCatching {
                val oldGroup = groupDao.findGroupById(id) ?: throw GroupNotFoundException()
                if (oldGroup.name == "admin" && patchRequest.name != null)
                    throw GroupUpdateFailedException(null, "Cannot update admin group name")

                val groupUpdateDTO = GroupUpdateDTO(
                    id = id,
                    name = patchRequest.name ?: oldGroup.name,
                    description = patchRequest.description ?: oldGroup.description,
                    strategyId = patchRequest.strategyId ?: oldGroup.strategyId,
                    config = if (patchRequest.config != null) {
                        GroupConfig(
                            groupStrategyConfig = patchRequest.config.groupStrategyConfig
                                ?: oldGroup.config.groupStrategyConfig
                        )
                    } else oldGroup.config
                )

                val influenceRow = groupDao.updateGroupById(groupUpdateDTO)
                if (influenceRow < 1) throw GroupNotFoundException()
            }.onFailure {
                if (it is GroupNotFoundException) throw GroupUpdateFailedException(it)
                else throw it
            }
        }
    }

    override suspend fun fetchGroup(id: Long): GroupVO {
        return dbQuery {
            val group = groupDao.findGroupById(id) ?: throw GroupNotFoundException()
            val strategy = strategyDao.findStrategyById(group.strategyId) ?: throw GroupNotFoundException()
            val roles = relationDao.listRoleByGroupId(group.id)
            GroupVO(
                id = group.id,
                name = group.name,
                description = group.description,
                groupConfig = group.config,
                strategyId = group.strategyId,
                strategyName = strategy.name,
                roles = roles,
                isSystemReserved = group.isSystemReserved,
                createTime = group.createTime
            )
        }
    }

    override suspend fun pageGroups(pageRequest: PageRequest): PageResult<GroupPageVO> {
        val pageResult = dbQuery { groupDao.pagination(pageRequest) }
        return pageResult
    }
}