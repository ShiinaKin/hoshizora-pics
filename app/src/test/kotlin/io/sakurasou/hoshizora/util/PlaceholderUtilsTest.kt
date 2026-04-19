package io.sakurasou.hoshizora.util

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * @author Shiina Kin
 * 2026/4/19 00:00
 */
class PlaceholderUtilsTest {
    @AfterTest
    fun tearDown() {
        unmockkObject(Clock.System)
    }

    @Test
    fun `parsePlaceholder should replace date time file and user placeholders`() {
        mockkObject(Clock.System)
        every { Clock.System.now() } returns Instant.fromEpochSeconds(0)

        val parsed =
            PlaceholderUtils.parsePlaceholder(
                namingRule = "{yyyy}/{MM}/{dd}/{timestamp}/{filename}/{user-id}",
                fileName = "photo.png",
                userId = 42L,
            )

        assertEquals("1970/01/01/0/photo.png/42", parsed)
    }

    @Test
    fun `parsePlaceholder should keep unknown placeholders unchanged`() {
        val parsed =
            PlaceholderUtils.parsePlaceholder(
                namingRule = "{unknown}/{filename}",
                fileName = "raw.jpg",
                userId = 1L,
            )

        assertEquals("{unknown}/raw.jpg", parsed)
    }

    @Test
    fun `parsePlaceholder should generate random placeholders with expected shape`() {
        val parsed =
            PlaceholderUtils.parsePlaceholder(
                namingRule = "{str-random-16}/{str-random-10}/{uniq}/{md5}",
                fileName = "photo.png",
                userId = 42L,
            )

        val parts = parsed.split("/")
        assertEquals(4, parts.size)
        assertTrue(parts[0].matches(Regex("[A-Za-z0-9]{16}")))
        assertTrue(parts[1].matches(Regex("[A-Za-z0-9]{10}")))
        assertTrue(parts[2].matches(Regex("[0-9a-f]{32}")))
        assertTrue(parts[3].matches(Regex("[0-9a-f]{32}")))
    }
}
