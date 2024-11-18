package io.sakurasou.service.system

import io.sakurasou.controller.vo.HoshizoraStatusVO
import io.sakurasou.controller.vo.SystemOverviewVO
import io.sakurasou.controller.vo.SystemStatisticsVO
import io.sakurasou.controller.vo.SystemStatusVO
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.user.UserDao
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.util.*

/**
 * @author Shiina Kin
 * 2024/11/18 20:18
 */
class SystemServiceImpl(
    private val imageDao: ImageDao,
    private val albumDao: AlbumDao,
    private val userDao: UserDao
) : SystemService {
    override suspend fun fetchSystemStatistics(): SystemStatisticsVO {
        return dbQuery {
            val (imageCount, totalSize) = imageDao.getImageCountAndTotalSize()
            val albumCount = albumDao.countAlbum()
            val userCount = userDao.countUser()
            SystemStatisticsVO(
                totalImageCount = imageCount,
                totalAlbumCount = albumCount,
                totalUserCount = userCount,
                totalUsedSpace = if (totalSize != 0L) totalSize / 1024 / 1024.0 else 0.0
            )
        }
    }

    override suspend fun fetchSystemOverview(): SystemOverviewVO {
        val buildRecord = Properties().also {
            it.load(this::class.java.classLoader.getResourceAsStream("buildRecord.properties"))
        }

        val hoshizoraStatus = HoshizoraStatusVO(
            buildTime = buildRecord["buildTime"] as String,
            commitId = buildRecord["commitId"] as String,
            version = buildRecord["version"] as String
        )

        val javaVersion = System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version")
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