package io.sakurasou.service.role

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.sakurasou.controller.vo.PermissionVO
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.permission.PermissionDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.role.RoleDao
import io.sakurasou.model.entity.Permission
import io.sakurasou.model.entity.Role
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author ShiinaKin
 * 2024/9/18 18:11
 */
class RoleServiceTest {
    private lateinit var roleDao: RoleDao
    private lateinit var permissionDao: PermissionDao
    private lateinit var relationDao: RelationDao
    private lateinit var roleService: RoleService
    private lateinit var instant: Instant

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        roleDao = mockk()
        permissionDao = mockk()
        relationDao = mockk()
        roleService = RoleServiceImpl(roleDao, permissionDao, relationDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
    }
}