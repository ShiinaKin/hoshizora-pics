package io.sakurasou.service.strategy

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import io.sakurasou.hoshizora.controller.request.StrategyInsertRequest
import io.sakurasou.hoshizora.controller.request.StrategyPatchRequest
import io.sakurasou.hoshizora.controller.vo.StrategyVO
import io.sakurasou.hoshizora.exception.service.strategy.StrategyNotFoundException
import io.sakurasou.hoshizora.model.DatabaseSingleton
import io.sakurasou.hoshizora.model.dao.group.GroupDao
import io.sakurasou.hoshizora.model.dao.strategy.StrategyDao
import io.sakurasou.hoshizora.model.dto.StrategyInsertDTO
import io.sakurasou.hoshizora.model.dto.StrategyUpdateDTO
import io.sakurasou.hoshizora.model.entity.Strategy
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.StrategyConfig
import io.sakurasou.hoshizora.service.strategy.StrategyService
import io.sakurasou.hoshizora.service.strategy.StrategyServiceImpl
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2024/9/12 13:54
 */
class StrategyServiceTest {
    private lateinit var strategyDao: StrategyDao
    private lateinit var groupDao: GroupDao
    private lateinit var strategyService: StrategyService
    private lateinit var instant: Instant
    private lateinit var now: LocalDateTime

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        strategyDao = mockk()
        groupDao = mockk()
        strategyService = StrategyServiceImpl(strategyDao, groupDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
        now = instant.toLocalDateTime(TimeZone.UTC)
    }

    @Test
    fun `save strategy should be successful`(): Unit =
        runBlocking {
            val json =
                """
                {
                    "uploadFolder": "/uploads",
                    "thumbnailFolder": "/thumbnails",
                    "type": "LOCAL",
                    "strategyType": "LOCAL"
                }
                """.trimIndent()
            val localStrategy = Json.decodeFromString<StrategyConfig>(json)
            val insertRequest = StrategyInsertRequest("Test Strategy", localStrategy)
            val exceptedInsertDTO = StrategyInsertDTO("Test Strategy", false, localStrategy, now, now)

            coEvery { DatabaseSingleton.dbQuery<Long>(any()) } coAnswers {
                this.arg<suspend () -> Long>(0).invoke()
            }
            every { strategyDao.saveStrategy(exceptedInsertDTO) } returns 2L

            strategyService.saveStrategy(insertRequest)

            assertEquals(2L, strategyDao.saveStrategy(exceptedInsertDTO))
        }

    @Test
    fun `delete should be successful`() =
        runBlocking {
            val strategyId = 1L
            val strategy =
                Strategy(
                    id = 1L,
                    name = "Test Strategy",
                    isSystemReserved = false,
                    config =
                        LocalStrategy(
                            uploadFolder = "/uploads",
                            thumbnailFolder = "/thumbnails",
                        ),
                    createTime = now,
                    updateTime = now,
                )

            coEvery { DatabaseSingleton.dbQuery<Int>(any()) } coAnswers {
                this.arg<suspend () -> Int>(0).invoke()
            }
            every { strategyDao.findStrategyById(strategyId) } returns strategy
            every { groupDao.doesGroupUsingStrategy(strategyId) } returns false
            every { strategyDao.deleteStrategyById(strategyId) } returns 1

            strategyService.deleteStrategy(strategyId)

            verify(exactly = 1) { strategyDao.deleteStrategyById(strategyId) }
        }

    @Test
    fun `update strategy should be successful`() =
        runBlocking {
            val strategy =
                Strategy(
                    id = 1L,
                    name = "Test Strategy",
                    isSystemReserved = false,
                    config =
                        LocalStrategy(
                            uploadFolder = "/uploads",
                            thumbnailFolder = "/thumbnails",
                        ),
                    createTime = now,
                    updateTime = now,
                )

            val json =
                """
                {
                    "name": "Test Patch Strategy",
                    "config": {
                        "uploadFolder": "/test-uploads",
                        "thumbnailFolder": "/test-thumbnails",
                        "type": "LOCAL",
                        "strategyType": "LOCAL"
                    }
                }
                """.trimIndent()
            val patchRequest = Json.decodeFromString<StrategyPatchRequest>(json)
            val exceptedConfig =
                LocalStrategy(
                    uploadFolder = "/test-uploads",
                    thumbnailFolder = "/test-thumbnails",
                )

            val exceptedUpdateDTO =
                StrategyUpdateDTO(
                    id = 1L,
                    name = "Test Patch Strategy",
                    config = exceptedConfig,
                    updateTime = now,
                )

            coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
                this.arg<suspend () -> Unit>(0).invoke()
            }
            every { strategyDao.findStrategyById(1L) } returns strategy
            every { strategyDao.updateStrategyById(exceptedUpdateDTO) } returns 1

            strategyService.updateStrategy(1L, patchRequest)

            verify(exactly = 1) { strategyDao.updateStrategyById(exceptedUpdateDTO) }
        }

    @Test
    fun `fetch existed strategy should be successful`() =
        runBlocking {
            val strategy =
                Strategy(
                    id = 1L,
                    name = "Test Strategy",
                    isSystemReserved = false,
                    config =
                        LocalStrategy(
                            uploadFolder = "/uploads",
                            thumbnailFolder = "/thumbnails",
                        ),
                    createTime = now,
                    updateTime = now,
                )
            val exceptedStrategyVO =
                StrategyVO(
                    id = strategy.id,
                    name = strategy.name,
                    config = strategy.config,
                    type = strategy.config.strategyType,
                    createTime = strategy.createTime,
                )

            coEvery { DatabaseSingleton.dbQuery<Strategy?>(any()) } coAnswers {
                this.arg<suspend () -> Strategy?>(0).invoke()
            }
            every { strategyDao.findStrategyById(1L) } returns strategy

            val result = strategyService.fetchStrategy(1L)

            assertEquals(exceptedStrategyVO, result)
        }

    @Test
    fun `fetch not exist strategy should throw StrategyNotExistException`(): Unit =
        runBlocking {
            coEvery { DatabaseSingleton.dbQuery<Strategy?>(any()) } coAnswers {
                this.arg<suspend () -> Strategy?>(0).invoke()
            }
            every { strategyDao.findStrategyById(2L) } returns null

            assertFailsWith(StrategyNotFoundException::class) {
                strategyService.fetchStrategy(2L)
            }
        }
}
