package io.sakurasou.service.system

import io.sakurasou.controller.vo.HoshizoraStatusVO
import io.sakurasou.controller.vo.SystemOverviewVO
import io.sakurasou.controller.vo.SystemStatusVO
import io.sakurasou.model.DatabaseSingleton.dbQuery
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.util.*

/**
 * @author Shiina Kin
 * 2024/11/18 20:18
 */
class SystemServiceImpl: SystemService {
    override suspend fun fetchSystemOverview(): SystemOverviewVO {
        val buildRecord = Properties().also {
            it.load(this::class.java.classLoader.getResourceAsStream("buildRecord.properties"))
        }

        val hoshizoraStatus = HoshizoraStatusVO(
            buildTime = buildRecord["buildTime"] as String,
            commitId = buildRecord["commitId"] as String,
            version = buildRecord["version"] as String
        )

        val javaVersion = System.getProperty("java.version")
        val databaseVersion = dbQuery {
            TransactionManager.current().exec("SELECT VERSION()") { resultSet ->
                resultSet.next()
                resultSet.getString(1)
            }
        }!!
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val serverTimeZone = TimeZone.currentSystemDefault().id
        val serverLanguage = Locale.getDefault().language

        val systemStatusVO = SystemStatusVO(
            javaVersion = javaVersion,
            databaseVersion = databaseVersion,
            operatingSystem = "$osName $osVersion",
            serverTimeZone = serverTimeZone,
            serverLanguage = serverLanguage
        )

        return SystemOverviewVO(
            hoshizoraStatus = hoshizoraStatus,
            systemStatus = systemStatusVO
        )
    }
}