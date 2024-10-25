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

    @Test
    fun `list all roles with permissions should be same as expected`(): Unit = runBlocking {
        val role1 = Role("role1", null)
        val role2 = Role("role2", "description2")
        val permission1 = Permission("permission1", "description1")
        val permission2 = Permission("permission2", null)
        val permission3 = Permission("permission3", "description3")
        val expected = listOf(
            RoleVO(role1.name, role1.description, listOf(
                PermissionVO(permission1.name, permission1.description),
                PermissionVO(permission2.name, permission2.description)
            )),
            RoleVO(role2.name, role2.description, listOf(
                PermissionVO(permission2.name, permission2.description),
                PermissionVO(permission3.name, permission3.description)
            ))
        )

        every { roleDao.listRoles() } returns listOf(role1, role2)
        every { relationDao.listPermissionByRole(role1.name) } returns listOf(permission1.name, permission2.name)
        every { relationDao.listPermissionByRole(role2.name) } returns listOf(permission2.name, permission3.name)
        every { permissionDao.findPermissionByName(permission1.name) } returns permission1
        every { permissionDao.findPermissionByName(permission2.name) } returns permission2
        every { permissionDao.findPermissionByName(permission3.name) } returns permission3
        coEvery { DatabaseSingleton.dbQuery<List<RoleVO>>(any()) } coAnswers {
            this.arg<suspend () -> List<RoleVO>>(0).invoke()
        }

        val rolesWithPermissions = roleService.listRolesWithPermissions()

        assertEquals(expected, rolesWithPermissions)
    }

    @Test
    fun `list assign roles with permissions should be same as expected`(): Unit = runBlocking {
        val role1 = Role("role1", null)
        val permission1 = Permission("permission1", "description1")
        val permission2 = Permission("permission2", null)
        val expected = listOf(
            RoleVO(role1.name, role1.description, listOf(
                PermissionVO(permission1.name, permission1.description),
                PermissionVO(permission2.name, permission2.description)
            ))
        )

        every { roleDao.findRoleByName(role1.name) } returns role1
        every { relationDao.listPermissionByRole(role1.name) } returns listOf(permission1.name, permission2.name)
        every { permissionDao.findPermissionByName(permission1.name) } returns permission1
        every { permissionDao.findPermissionByName(permission2.name) } returns permission2
        coEvery { DatabaseSingleton.dbQuery<List<RoleVO>>(any()) } coAnswers {
            this.arg<suspend () -> List<RoleVO>>(0).invoke()
        }

        val rolesWithPermissions = roleService.listRolesWithPermissionsOfUser(listOf(role1.name))

        assertEquals(expected, rolesWithPermissions)
    }
}