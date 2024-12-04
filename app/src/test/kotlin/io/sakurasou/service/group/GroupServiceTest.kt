package io.sakurasou.service.group

import io.mockk.*
import io.sakurasou.controller.request.GroupConfigUpdatePatch
import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.vo.GroupVO
import io.sakurasou.exception.service.group.GroupNotFoundException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO
import io.sakurasou.model.entity.Group
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.model.group.GroupStrategyConfig
import io.sakurasou.model.strategy.LocalStrategy
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
    private lateinit var userDao: UserDao
    private lateinit var strategyDao: StrategyDao
    private lateinit var relationDao: RelationDao
    private lateinit var groupService: GroupService
    private lateinit var instant: Instant
    private lateinit var now: LocalDateTime

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        groupDao = mockk()
        userDao = mockk()
        strategyDao = mockk()
        relationDao = mockk()
        groupService = GroupServiceImpl(groupDao, userDao, strategyDao, relationDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
        now = instant.toLocalDateTime(TimeZone.UTC)
    }

    @Test
    fun `save group should be successful`() = runBlocking {
        val insertRequest = GroupInsertRequest(
            name = "Test Group",
            description = "test",
            config = GroupConfig(GroupStrategyConfig()),
            strategyId = 1,
            roles = listOf("user")
        )
        val exceptedInsertDTO = GroupInsertDTO(
            name = "Test Group",
            description = "test",
            strategyId = 1,
            config = GroupConfig(GroupStrategyConfig()),
            isSystemReserved = false,
            createTime = now
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
        val group = Group(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            config = GroupConfig(GroupStrategyConfig()),
            isSystemReserved = false,
            createTime = now
        )

        coEvery { DatabaseSingleton.dbQuery<Int>(any()) } coAnswers {
            this.arg<suspend () -> Int>(0).invoke()
        }
        every { groupDao.findGroupById(1L) } returns group
        every { userDao.doesUsersBelongToUserGroup(group.id) } returns false
        every { relationDao.deleteGroupToRolesByGroupId(group.id) } just Runs
        every { groupDao.deleteGroupById(group.id) } returns 1

        groupService.deleteGroup(1)

        verify(exactly = 1) { groupDao.deleteGroupById(1) }
    }

    @Test
    fun `update group should be successful`() = runBlocking {
        val group = Group(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            config = GroupConfig(GroupStrategyConfig()),
            isSystemReserved = false,
            createTime = now
        )
        val patchRequest = GroupPatchRequest(
            name = "Test Group",
            description = "test",
            config = GroupConfigUpdatePatch(
                groupStrategyConfig = null
            )
        )

        val exceptedUpdateDTO = GroupUpdateDTO(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            config = GroupConfig(GroupStrategyConfig())
        )

        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
        every { groupDao.findGroupById(1) } returns group
        every { groupDao.updateGroupById(exceptedUpdateDTO) } returns 1

        groupService.patchGroup(1, patchRequest)

        verify(exactly = 1) { groupDao.updateGroupById(exceptedUpdateDTO) }
    }

    @Test
    fun `fetch group should be successful`() = runBlocking {
        val group = Group(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            config = GroupConfig(GroupStrategyConfig()),
            isSystemReserved = false,
            createTime = now
        )
        val strategy = Strategy(
            id = 1L,
            name = "Test Strategy",
            isSystemReserved = false,
            config = LocalStrategy(
                uploadFolder = "/uploads",
                thumbnailFolder = "/thumbnails"
            ),
            createTime = now,
            updateTime = now
        )
        val roles = listOf("user")

        val exceptedGroupVO = GroupVO(
            id = 1,
            name = "Test Group",
            description = "test",
            strategyId = 1,
            strategyName = "Test Strategy",
            groupConfig = GroupConfig(GroupStrategyConfig()),
            roles = listOf("user"),
            isSystemReserved = false,
            createTime = now
        )

        coEvery { DatabaseSingleton.dbQuery<GroupVO>(any()) } coAnswers {
            this.arg<suspend () -> GroupVO>(0).invoke()
        }
        every { groupDao.findGroupById(1) } returns group
        every { strategyDao.findStrategyById(group.strategyId) } returns strategy
        every { relationDao.listRoleByGroupId(group.id) } returns roles

        val result = groupService.fetchGroup(1)

        assertEquals(exceptedGroupVO, result)
    }

    @Test
    fun `fetch not exist group should throw GroupNotExistException`(): Unit = runBlocking {
        coEvery { DatabaseSingleton.dbQuery<Group?>(any()) } coAnswers {
            this.arg<suspend () -> Group?>(0).invoke()
        }
        every { groupDao.findGroupById(1) } returns null

        assertFailsWith<GroupNotFoundException> {
            groupService.fetchGroup(1)
        }
    }
}