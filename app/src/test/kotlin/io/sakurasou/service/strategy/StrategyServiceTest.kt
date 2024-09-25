package io.sakurasou.service.strategy

import io.mockk.*
import io.sakurasou.controller.request.StrategyInsertRequest
import io.sakurasou.controller.request.StrategyPatchRequest
import io.sakurasou.controller.vo.StrategyVO
import io.sakurasou.exception.service.strategy.StrategyNotFoundException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dto.StrategyInsertDTO
import io.sakurasou.model.dto.StrategyUpdateDTO
import io.sakurasou.model.entity.Strategy
import io.sakurasou.model.strategy.LocalStrategy
import io.sakurasou.model.strategy.StrategyConfig
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
    private lateinit var strategyService: StrategyService
    private lateinit var instant: Instant
    private lateinit var now: LocalDateTime

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        strategyDao = mockk()
        strategyService = StrategyServiceImpl(strategyDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
        now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    @Test
    fun `save strategy should be successful`(): Unit = runBlocking {
        val json = """
            {
                "uploadFolder": "/uploads",
                "type": "LOCAL",
                "strategyType": "LOCAL"
            }
        """.trimIndent()
        val localStrategy = Json.decodeFromString<StrategyConfig>(json)
        val insertRequest = StrategyInsertRequest("Test Strategy", localStrategy)
        val exceptedInsertDTO = StrategyInsertDTO("Test Strategy", localStrategy, now, now)

        coEvery { DatabaseSingleton.dbQuery<Long>(any()) } coAnswers {
            this.arg<suspend () -> Long>(0).invoke()
        }
        every { strategyDao.saveStrategy(exceptedInsertDTO) } returns 2L

        strategyService.saveStrategy(insertRequest)

        assertEquals(2L, strategyDao.saveStrategy(exceptedInsertDTO))
    }

    @Test
    fun `delete should be successful`() = runBlocking {
        coEvery { DatabaseSingleton.dbQuery<Int>(any()) } coAnswers {
            this.arg<suspend () -> Int>(0).invoke()
        }
        every { strategyDao.deleteStrategyById(1L) } returns 1

        strategyService.deleteStrategy(1L)

        verify(exactly = 1) { strategyDao.deleteStrategyById(1L) }
    }

    @Test
    fun updateStrategy_updatesSuccessfully() = runBlocking {
        val json = """
            {
                "name": "Test Patch Strategy",
                "config": null
            }
        """.trimIndent()
        val patchRequest = Json.decodeFromString<StrategyPatchRequest>(json)
        val oldStrategyConfig = LocalStrategy("/uploads")
        val oldStrategy = StrategyVO(
            id = 1L,
            name = "Test Strategy",
            config = oldStrategyConfig,
            type = oldStrategyConfig.strategyType,
            createTime = now
        )

        val exceptedUpdateDTO = StrategyUpdateDTO(
            id = 1L,
            name = "Test Patch Strategy",
            config = oldStrategyConfig,
            updateTime = now
        )

        strategyService = spyk(strategyService)
        coEvery { strategyService.fetchStrategy(1L) } returns oldStrategy
        coEvery { DatabaseSingleton.dbQuery<Int>(any()) } coAnswers {
            this.arg<suspend () -> Int>(0).invoke()
        }
        every { strategyDao.updateStrategyById(exceptedUpdateDTO) } returns 1

        strategyService.updateStrategy(1L, patchRequest)

        verify(exactly = 1) { strategyDao.updateStrategyById(exceptedUpdateDTO) }
    }

    @Test
    fun `fetch existed strategy should be successful`() = runBlocking {
        val strategy = Strategy(
            id = 1L,
            name = "Test Strategy",
            config = LocalStrategy("/uploads"),
            createTime = now,
            updateTime = now
        )
        val exceptedStrategyVO = StrategyVO(
            id = strategy.id,
            name = strategy.name,
            config = strategy.config,
            type = strategy.config.strategyType,
            createTime = strategy.createTime
        )

        coEvery { DatabaseSingleton.dbQuery<Strategy?>(any()) } coAnswers {
            this.arg<suspend () -> Strategy?>(0).invoke()
        }
        every { strategyDao.findStrategyById(1L) } returns strategy

        val result = strategyService.fetchStrategy(1L)

        assertEquals(exceptedStrategyVO, result)
    }

    @Test
    fun `fetch not exist strategy should throw StrategyNotExistException`(): Unit = runBlocking {
        coEvery { DatabaseSingleton.dbQuery<Strategy?>(any()) } coAnswers {
            this.arg<suspend () -> Strategy?>(0).invoke()
        }
        every { strategyDao.findStrategyById(2L) } returns null

        assertFailsWith(StrategyNotFoundException::class) {
            strategyService.fetchStrategy(2L)
        }
    }
}