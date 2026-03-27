package io.sakurasou.hoshizora.controller.vo

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/11/18 21:50
 */

@Serializable
data class SystemStatisticsVO(
    val totalImageCount: Long,
    val totalAlbumCount: Long,
    val totalUserCount: Long,
    val totalUsedSpace: Double,
)

@Serializable
data class SystemOverviewVO(
    val hoshizoraStatus: HoshizoraStatusVO,
    val systemStatus: SystemStatusVO,
)

@Serializable
data class HoshizoraStatusVO(
    val version: String,
    val buildTime: String,
    val commitId: String,
)

@Serializable
data class SystemStatusVO(
    val javaVersion: String,
    val databaseVersion: String,
    val operatingSystem: String,
    val serverTimeZone: String,
    val serverLanguage: String,
)
