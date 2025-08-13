package io.sakurasou.hoshizora.service.permission

import io.sakurasou.hoshizora.controller.vo.PermissionVO
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.dao.permission.PermissionDao

/**
 * @author Shiina Kin
 * 2024/12/2 17:58
 */
class PermissionServiceImpl(
    private val permissionDao: PermissionDao,
) : PermissionService {
    override suspend fun fetchAllPermissions(): List<PermissionVO> =
        dbQuery {
            permissionDao.findAllPermissions().map {
                PermissionVO(
                    name = it.name,
                    description = it.description,
                )
            }
        }
}
