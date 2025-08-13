package io.sakurasou.service.role

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.sakurasou.hoshizora.model.DatabaseSingleton
import io.sakurasou.hoshizora.model.dao.permission.PermissionDao
import io.sakurasou.hoshizora.model.dao.relation.RelationDao
import io.sakurasou.hoshizora.model.dao.role.RoleDao
import io.sakurasou.hoshizora.service.role.RoleService
import io.sakurasou.hoshizora.service.role.RoleServiceImpl
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest

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
