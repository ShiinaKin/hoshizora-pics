package io.sakurasou.service.group

import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.GroupPageVO
import io.sakurasou.controller.vo.GroupVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.GroupNotExistException
import io.sakurasou.exception.RoleNotExistException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO

/**
 * @author Shiina Kin
 * 2024/9/24 12:41
 */
class GroupServiceImpl(
    private val groupDao: GroupDao,
    private val relationDao: RelationDao
) : GroupService {
    override suspend fun saveGroup(insertRequest: GroupInsertRequest) {
        val groupInsertDTO = GroupInsertDTO(
            name = insertRequest.name,
            description = insertRequest.description,
            strategyId = insertRequest.strategyId,
            maxSize = insertRequest.maxSize
        )
        val groupRoles = insertRequest.roles

        dbQuery {
            val groupId = groupDao.saveGroup(groupInsertDTO)
            runCatching {
                relationDao.batchInsertGroupToRoles(groupId, groupRoles)
            }.onFailure {
                throw RoleNotExistException()
            }
        }
    }

    override suspend fun deleteGroup(id: Long) {
        dbQuery { groupDao.deleteGroupById(id) }
    }

    override suspend fun updateGroup(id: Long, patchRequest: GroupPatchRequest) {
        val oldGroup = fetchGroup(id)

        val groupUpdateDTO = GroupUpdateDTO(
            id = id,
            name = patchRequest.name ?: oldGroup.name,
            description = patchRequest.description ?: oldGroup.description,
            strategyId = patchRequest.strategyId ?: oldGroup.strategyId,
            maxSize = patchRequest.maxSize ?: oldGroup.maxSize
        )

        dbQuery { groupDao.updateGroupById(groupUpdateDTO) }
    }

    override suspend fun fetchGroup(id: Long): GroupVO {
        val group = dbQuery { groupDao.findGroupById(id) } ?: throw GroupNotExistException()
        val roles = dbQuery { relationDao.listRoleByGroupId(group.id) }
        return GroupVO(
            id = group.id,
            name = group.name,
            description = group.description,
            strategyId = group.strategyId,
            maxSize = group.maxSize,
            roles = roles
        )
    }

    override suspend fun pageGroups(pageRequest: PageRequest): PageResult<GroupPageVO> {
        val groupPageResult = dbQuery { groupDao.pagination(pageRequest) }
        val pageVOList = groupPageResult.data.map {
            GroupPageVO(
                id = it.id,
                name = it.name,
                strategyId = it.strategyId,
            )
        }
        return PageResult(
            page = groupPageResult.page,
            pageSize = groupPageResult.pageSize,
            total = groupPageResult.total,
            data = pageVOList
        )
    }
}