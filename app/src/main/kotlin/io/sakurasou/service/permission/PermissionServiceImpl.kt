package io.sakurasou.service.permission

import io.sakurasou.controller.vo.PermissionVO
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.permission.PermissionDao

/**
 * @author Shiina Kin
 * 2024/12/2 17:58
 */
class PermissionServiceImpl(
    private val permissionDao: PermissionDao
) : PermissionService {
    override suspend fun fetchAllPermissions(): List<PermissionVO> {
        return dbQuery {
            permissionDao.findAllPermissions().map {
                PermissionVO(
                    name = it.name,
                    description = it.description
                )
            }
        }
    }
}