package io.sakurasou.hoshizora.controller

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.StrategyInsertRequest
import io.sakurasou.hoshizora.controller.request.StrategyPatchRequest
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.service.strategy.StrategyService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StrategyControllerTest {
    @Test
    fun `strategy routes delegate to strategy service`() {
        val strategyService = mockk<StrategyService>()
        val config = LocalStrategy(uploadFolder = "/uploads", thumbnailFolder = "/thumbnails")
        val insertRequest = StrategyInsertRequest(name = "local", config = config)
        val patchRequest = StrategyPatchRequest(name = "local-new", config = config)

        coEvery { strategyService.saveStrategy(insertRequest) } returns Unit
        coEvery { strategyService.deleteStrategy(1L) } returns Unit
        coEvery { strategyService.updateStrategy(1L, patchRequest) } returns Unit
        coEvery { strategyService.fetchStrategy(1L) } returns ControllerTestFixtures.strategyVO()
        coEvery { strategyService.pageStrategies(any()) } returns ControllerTestFixtures.strategyPageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    strategyRoute(strategyService)
                }
            },
        ) { client ->
            assertEquals(
                HttpStatusCode.OK,
                client.post("/api/strategy") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }.status,
            )
            assertEquals(HttpStatusCode.OK, client.delete("/api/strategy/1").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/strategy/1") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val fetchResponse = client.get("/api/strategy/1")
            val pageResponse = client.get("/api/strategy/page?page=1&pageSize=20")

            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"name\":\"local\""))
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { strategyService.saveStrategy(insertRequest) }
            coVerify(exactly = 1) { strategyService.deleteStrategy(1L) }
            coVerify(exactly = 1) { strategyService.updateStrategy(1L, patchRequest) }
            coVerify(exactly = 1) { strategyService.fetchStrategy(1L) }
            coVerify(exactly = 1) {
                strategyService.pageStrategies(
                    match {
                        it.page == 1L && it.pageSize == 20
                    },
                )
            }
        }
    }

    @Test
    fun `strategy insert rejects invalid s3 endpoint before service call`() {
        val strategyService = mockk<StrategyService>()
        val request =
            StrategyInsertRequest(
                name = "bad-s3",
                config =
                    S3Strategy(
                        endpoint = "not-a-url",
                        bucketName = "bucket",
                        region = "us-east-1",
                        accessKey = "access",
                        secretKey = "secret",
                        uploadFolder = "/uploads",
                        thumbnailFolder = "/thumbnails",
                        publicUrl = "https://cdn.example.com",
                    ),
            )

        testControllerApplication(
            routes = {
                route("api") {
                    strategyRoute(strategyService)
                }
            },
        ) { client ->
            val response =
                client.post("/api/strategy") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertFalse(response.compactBody().contains("\"isSuccessful\":true"))
            coVerify(exactly = 0) { strategyService.saveStrategy(any()) }
        }
    }
}
