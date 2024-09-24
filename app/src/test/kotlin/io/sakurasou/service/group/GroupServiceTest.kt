package io.sakurasou.service.group

import io.mockk.*
import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.vo.GroupVO
import io.sakurasou.exception.GroupNotExistException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO
import io.sakurasou.model.entity.Group
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2024/9/12 13:54
 */
class GroupServiceTest {
    private lateinit var groupDao: GroupDao
    private lateinit var relationDao: RelationDao
    private lateinit var groupService: GroupService
    private lateinit var instant: Instant
    private lateinit var now: LocalDateTime

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        groupDao = mockk()
        relationDao = mockk()
        groupService = GroupServiceImpl(groupDao, relationDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
        now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    @Test
    fun `save group should be successful`() = runBlocking {
        val insertRequest = GroupInsertRequest(
            name = "Test Group",
            description = "test",
            strategyId = 1,
            maxSize = 5 * 1024 * 1024L,
            roles = listOf("user")
        )
        val exceptedInsertDTO = GroupInsertDTO(
            name = "Test Group",
            description = "test",
            strategyId = 1,
            maxSize = 5 * 1024 * 1024L
        )
        val exceptedRoles = listOf("user")

        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
        every { groupDao.saveGroup(exceptedInsertDTO) } returns 1
        every { relationDao.batchInsertGroupToRoles(1, exceptedRoles) } just Runs

        groupService.saveGroup(insertRequest)

        verify(exactly = 1) { groupDao.saveGroup(exceptedInsertDTO) }
        verify(exactly = 1) { relationDao.batchInsertGroupToRoles(1, exceptedRoles) }
    }

    @Test
    fun `delete group should be successful`() = runBlocking {
        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
        every { groupDao.deleteGroupById(1) } just Runs

        groupService.deleteGroup(1)

        verify(exactly = 1) { groupDao.deleteGroupById(1) }
    }

    @Test
    fun `update group should be successful`() = runBlocking {
        groupService = spyk(groupService)

        val oldGroup = GroupVO(
            id = 1,
            name = "Old Group",
            description = null,
            strategyId = 1,
            maxSize = 5 * 1024 * 1024L,
            roles = listOf("user")
        )
        val patchRequest = GroupPatchRequest(
            name = "Test Group",
            description = "test",
            maxSize = 1 * 1024L
        )

        val exceptedUpdateDTO = GroupUpdateDTO(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            maxSize = 1 * 1024L
        )

        coEvery { groupService.fetchGroup(1) } returns oldGroup
        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
        every { groupDao.updateGroupById(exceptedUpdateDTO) } just Runs

        groupService.updateGroup(1, patchRequest)

        verify(exactly = 1) { groupDao.updateGroupById(exceptedUpdateDTO) }
    }

    @Test
    fun `fetch group should be successful`() = runBlocking {
        val group = Group(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            maxSize = 5 * 1024 * 1024L,
        )
        val roles = listOf("user")

        val exceptedGroupVO = GroupVO(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            maxSize = 5 * 1024 * 1024L,
            roles = listOf("user")
        )

        coEvery { DatabaseSingleton.dbQuery<Group?>(any()) } coAnswers {
            this.arg<suspend () -> Group?>(0).invoke()
        }
        coEvery { DatabaseSingleton.dbQuery<List<String>>(any()) } coAnswers {
            this.arg<suspend () -> List<String>>(0).invoke()
        }
        every { groupDao.findGroupById(1) } returns group
        every { relationDao.listRoleByGroupId(1) } returns roles

        val result = groupService.fetchGroup(1)

        assertEquals(exceptedGroupVO, result)
    }

    @Test
    fun `fetch not exist group should throw GroupNotExistException`(): Unit = runBlocking {
        coEvery { DatabaseSingleton.dbQuery<Group?>(any()) } coAnswers {
            this.arg<suspend () -> Group?>(0).invoke()
        }
        every { groupDao.findGroupById(1) } returns null

        assertFailsWith<GroupNotExistException> {
            groupService.fetchGroup(1)
        }
    }
}