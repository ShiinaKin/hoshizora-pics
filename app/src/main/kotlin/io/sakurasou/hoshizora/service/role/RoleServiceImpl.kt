package io.sakurasou.hoshizora.service.role

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.RoleInsertRequest
import io.sakurasou.hoshizora.controller.request.RolePatchRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.PermissionVO
import io.sakurasou.hoshizora.controller.vo.RolePageVO
import io.sakurasou.hoshizora.controller.vo.RoleVO
import io.sakurasou.hoshizora.exception.service.permission.PermissionNotFoundException
import io.sakurasou.hoshizora.exception.service.role.RoleDeleteFailedException
import io.sakurasou.hoshizora.exception.service.role.RoleNotFoundException
import io.sakurasou.hoshizora.exception.service.role.RoleUpdateFailedException
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.dao.permission.PermissionDao
import io.sakurasou.hoshizora.model.dao.relation.RelationDao
import io.sakurasou.hoshizora.model.dao.role.RoleDao
import io.sakurasou.hoshizora.model.dto.RoleInsertDTO
import io.sakurasou.hoshizora.model.dto.RoleUpdateDTO
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author ShiinaKin
 * 2024/9/18 17:30
 */
class RoleServiceImpl(
    private val roleDao: RoleDao,
    private val permissionDao: PermissionDao,
    private val relationDao: RelationDao,
) : RoleService {
    override suspend fun saveRole(insertRequest: RoleInsertRequest) {
        dbQuery {
            val insertDTO =
                RoleInsertDTO(
                    name = insertRequest.name,
                    displayName = insertRequest.displayName,
                    isSystemReserved = false,
                    description = insertRequest.description,
                    createTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                )
            roleDao.saveRole(insertDTO)

            insertRequest.permissions.forEach { permission ->
                permissionDao.findPermissionByName(permission) ?: throw PermissionNotFoundException()
            }
            relationDao.batchInsertRoleToPermissions(insertRequest.name, insertRequest.permissions)
        }
    }

    override suspend fun patchRole(
        roleName: String,
        patchRequest: RolePatchRequest,
    ) {
        runCatching {
            dbQuery {
                val oldRole = roleDao.findRoleByName(roleName) ?: throw RoleNotFoundException()
                val updateDTO =
                    RoleUpdateDTO(
                        displayName = patchRequest.displayName ?: oldRole.displayName,
                        description = patchRequest.description ?: oldRole.description,
                    )
                val influenceRowCnt = roleDao.patchRole(roleName, updateDTO)
                if (influenceRowCnt < 1) throw RoleNotFoundException()
            }
        }.onFailure { e ->
            if (e is RoleNotFoundException) {
                throw RoleUpdateFailedException(e)
            } else {
                throw e
            }
        }
    }

    override suspend fun deleteRole(roleName: String) {
        runCatching {
            dbQuery {
                val influenceRowCnt = roleDao.deleteRole(roleName)
                if (influenceRowCnt < 1) throw RoleNotFoundException()
            }
        }.onFailure { e ->
            if (e is RoleNotFoundException) {
                throw RoleDeleteFailedException(e)
            } else {
                throw e
            }
        }
    }

    override suspend fun listRolesWithPermissionsOfUser(groupId: Long): List<RoleVO> =
        dbQuery {
            val roleNames = relationDao.listRoleByGroupId(groupId)
            roleNames.map { roleName ->
                val role = roleDao.findRoleByName(roleName) ?: throw RoleNotFoundException()
                val permissionNames = relationDao.listPermissionByRole(roleName)
                val permissionVOList =
                    permissionNames.map { permissionName ->
                        val permission =
                            permissionDao.findPermissionByName(permissionName)
                                ?: throw PermissionNotFoundException()
                        PermissionVO(
                            permission.name,
                            permission.description,
                        )
                    }
                RoleVO(
                    name = role.name,
                    displayName = role.displayName,
                    description = role.description,
                    permissions = permissionVOList,
                )
            }
        }

    override suspend fun fetchRole(roleName: String): RoleVO =
        dbQuery {
            val role = roleDao.findRoleByName(roleName) ?: throw RoleNotFoundException()
            val permissionNames = relationDao.listPermissionByRole(roleName)
            val permissionVOList =
                permissionNames.map { permissionName ->
                    val permission =
                        permissionDao.findPermissionByName(permissionName)
                            ?: throw PermissionNotFoundException()
                    PermissionVO(
                        permission.name,
                        permission.description,
                    )
                }
            RoleVO(
                name = role.name,
                displayName = role.displayName,
                description = role.description,
                permissions = permissionVOList,
            )
        }

    override suspend fun pageRoles(pageRequest: PageRequest): PageResult<RolePageVO> = dbQuery { roleDao.pagination(pageRequest) }
}
