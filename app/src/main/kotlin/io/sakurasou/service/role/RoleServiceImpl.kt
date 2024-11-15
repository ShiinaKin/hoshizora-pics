package io.sakurasou.service.role

import io.sakurasou.controller.vo.PermissionVO
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.exception.service.permission.PermissionNotFoundException
import io.sakurasou.exception.service.role.RoleNotFoundException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.permission.PermissionDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.role.RoleDao

/**
 * @author ShiinaKin
 * 2024/9/18 17:30
 */
class RoleServiceImpl(
    private val roleDao: RoleDao,
    private val permissionDao: PermissionDao,
    private val relationDao: RelationDao
) : RoleService {
    override suspend fun listRolesWithPermissions(): List<RoleVO> {
        return dbQuery {
            val roles = roleDao.listRoles()
            roles.map { role ->
                val permissionNames = relationDao.listPermissionByRole(role.name)
                val permissionVOList = permissionNames.map { permissionName ->
                    val permission = permissionDao.findPermissionByName(permissionName)
                        ?: throw PermissionNotFoundException()
                    PermissionVO(
                        permission.name,
                        permission.description
                    )
                }
                RoleVO(
                    role.name,
                    role.description,
                    permissionVOList
                )
            }
        }
    }

    override suspend fun listRolesWithPermissionsOfUser(groupId: Long): List<RoleVO> {
        return dbQuery {
            val roleNames = relationDao.listRoleByGroupId(groupId)
            roleNames.map { roleName ->
                val role = roleDao.findRoleByName(roleName) ?: throw RoleNotFoundException()
                val permissionNames = relationDao.listPermissionByRole(roleName)
                val permissionVOList = permissionNames.map { permissionName ->
                    val permission = permissionDao.findPermissionByName(permissionName)
                        ?: throw PermissionNotFoundException()
                    PermissionVO(
                        permission.name,
                        permission.description
                    )
                }
                RoleVO(
                    role.name,
                    role.description,
                    permissionVOList
                )
            }
        }
    }
}