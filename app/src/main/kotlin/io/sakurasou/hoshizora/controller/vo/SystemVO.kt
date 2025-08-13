package io.sakurasou.hoshizora.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/11/18 21:50
 */

@Serializable
@Name("SystemStatisticsVO")
data class SystemStatisticsVO(
    val totalImageCount: Long,
    val totalAlbumCount: Long,
    val totalUserCount: Long,
    val totalUsedSpace: Double,
)

@Serializable
@Name("SystemOverviewVO")
data class SystemOverviewVO(
    val hoshizoraStatus: HoshizoraStatusVO,
    val systemStatus: SystemStatusVO,
)

@Serializable
@Name("HoshizoraStatusVO")
data class HoshizoraStatusVO(
    val version: String,
    val buildTime: String,
    val commitId: String,
)

@Serializable
@Name("SystemStatusVO")
data class SystemStatusVO(
    val javaVersion: String,
    val databaseVersion: String,
    val operatingSystem: String,
    val serverTimeZone: String,
    val serverLanguage: String,
)
