package io.sakurasou.model.common

import io.github.oshai.kotlinlogging.KotlinLogging
import io.sakurasou.constant.*
import io.sakurasou.di.InstanceCenter
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.permission.Permissions
import io.sakurasou.model.dao.personalAccessToken.PersonalAccessTokens
import io.sakurasou.model.dao.relation.GroupRoles
import io.sakurasou.model.dao.relation.PersonalAccessTokenPermissions
import io.sakurasou.model.dao.relation.RolePermissions
import io.sakurasou.model.dao.role.Roles
import io.sakurasou.model.dao.setting.Settings
import io.sakurasou.model.dao.strategy.Strategies
import io.sakurasou.model.dao.user.Users
import io.sakurasou.model.dto.*
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.model.group.GroupStrategyConfig
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.SystemSetting
import io.sakurasou.model.setting.SystemStatus
import io.sakurasou.model.strategy.LocalStrategy
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SchemaUtils

/**
 * @author Shiina Kin
 * 2024/9/12 16:22
 */
private val logger = KotlinLogging.logger {}

object DatabaseInit {
    fun init(version: String) {
        runBlocking {
            dbQuery {
                if (!isFirstRunning()) {
                    SchemaUtils.create(Images)
                    SchemaUtils.create(Albums)
                    SchemaUtils.create(Strategies)
                    SchemaUtils.create(Settings)
                    SchemaUtils.create(Users)
                    SchemaUtils.create(Groups)
                    SchemaUtils.create(Roles)
                    SchemaUtils.create(Permissions)
                    SchemaUtils.create(GroupRoles)
                    SchemaUtils.create(RolePermissions)
                    SchemaUtils.create(PersonalAccessTokens)
                    SchemaUtils.create(PersonalAccessTokenPermissions)
                    initStrategy()
                    initSetting(version)
                    initPermission()
                    initRole()
                    initGroup()
                    initRelation()
                }
            }
            InstanceCenter.initSystemStatus()
        }
    }

    private fun isFirstRunning(): Boolean {
        return SchemaUtils.listTables().size == 12
    }

    private fun initPermission() {
        val allPermissions = listOf(
            userOpsPermissions,
            groupOpsPermissions,
            roleOpsPermissions,
            permissionOpsPermissions,
            settingOpsPermissions,
            strategyOpsPermissions,
            imageOpsPermissions,
            albumOpsPermissions,
            personalAccessTokenOpsPermissions
        )
            .flatten()
            .map { PermissionInsertDTO(it, null) }

        InstanceCenter.permissionDao.batchSavePermission(allPermissions)

        logger.info { "permission init success" }
    }

    private fun initRole() {
        val adminRoleInsertDTO = RoleInsertDTO(ROLE_ADMIN, "Role for admin", true, null)
        val userRoleInsertDTO = RoleInsertDTO(ROLE_USER, "Role for user", true, null)
        InstanceCenter.roleDao.saveRole(adminRoleInsertDTO)
        InstanceCenter.roleDao.saveRole(userRoleInsertDTO)

        logger.info { "role init success" }
    }

    private fun initStrategy() {
        val localStrategyConfig = LocalStrategy("uploads", "thumbnails")
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val strategyInsertDTO = StrategyInsertDTO(
            name = "local",
            isSystemReserved = true,
            config = localStrategyConfig,
            createTime = now,
            updateTime = now
        )
        InstanceCenter.strategyDao.saveStrategy(strategyInsertDTO)

        logger.info { "strategy init success" }
    }

    private fun initGroup() {
        val adminGroupConfig = GroupConfig(
            groupStrategyConfig = GroupStrategyConfig()
        )
        val adminGroup = GroupInsertDTO(
            name = GROUP_ADMIN,
            description = "admin group",
            strategyId = 1,
            config = adminGroupConfig,
            isSystemReserved = true,
            createTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
        val userGroupConfig = GroupConfig(
            groupStrategyConfig = GroupStrategyConfig()
        )
        val userGroup = GroupInsertDTO(
            name = GROUP_USER,
            description = "user group",
            strategyId = 1,
            config = userGroupConfig,
            isSystemReserved = true,
            createTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
        InstanceCenter.groupDao.saveGroup(adminGroup)
        InstanceCenter.groupDao.saveGroup(userGroup)

        logger.info { "group init success" }
    }

    private fun initRelation() {
        // role-permission
        val adminPermissions = listOf(
            userOpsPermissions,
            groupOpsPermissions,
            settingOpsPermissions,
            roleOpsPermissions,
            strategyOpsPermissions,
            imageOpsPermissions,
            albumOpsPermissions,
            personalAccessTokenOpsPermissions
        ).flatten()
        val userPermissions = listOf(
            USER_READ_SELF,
            USER_WRITE_SELF,
            ROLE_READ_SELF,
            IMAGE_READ_SELF_SINGLE,
            IMAGE_READ_SELF_ALL,
            IMAGE_WRITE_SELF,
            IMAGE_DELETE_SELF,
            ALBUM_READ_SELF_SINGLE,
            ALBUM_READ_SELF_ALL,
            ALBUM_WRITE_SELF,
            ALBUM_DELETE_SELF,
            PERSONAL_ACCESS_TOKEN_READ_SELF,
            PERSONAL_ACCESS_TOKEN_WRITE_SELF
        )

        InstanceCenter.relationDao.batchInsertRoleToPermissions(ROLE_ADMIN, adminPermissions)
        InstanceCenter.relationDao.batchInsertRoleToPermissions(ROLE_USER, userPermissions)

        logger.info { "role-permission relation init success" }

        // group-role
        val adminRoles = listOf(ROLE_ADMIN)
        val userRoles = listOf(ROLE_USER)
        InstanceCenter.relationDao.batchInsertGroupToRoles(1, adminRoles)
        InstanceCenter.relationDao.batchInsertGroupToRoles(2, userRoles)

        logger.info { "group-role relation init success" }
    }

    private fun initSetting(version: String) {
        val siteSettingConfig = SiteSetting(
            siteExternalUrl = "http://localhost:8080",
            siteTitle = "HoshizoraPics",
            siteSubtitle = "A simple pic management",
            siteDescription = "A simple pic management"
        )
        val systemSettingConfig = SystemSetting(
            defaultGroupId = 2,
            allowSignup = false,
            allowRandomFetch = false
        )
        val systemStatus = SystemStatus(
            isInit = false,
            version = version
        )

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

        val siteSettingInsertDTO = SettingInsertDTO(SETTING_SITE, siteSettingConfig, now, now)
        val systemSettingInsertDTO = SettingInsertDTO(SETTING_SYSTEM, systemSettingConfig, now, now)
        val systemStatusInsertDTO = SettingInsertDTO(SETTING_STATUS, systemStatus, now, now)

        InstanceCenter.settingDao.saveSetting(siteSettingInsertDTO)
        InstanceCenter.settingDao.saveSetting(systemSettingInsertDTO)
        InstanceCenter.settingDao.saveSetting(systemStatusInsertDTO)

        logger.info { "setting init success" }
    }

}

private val userOpsPermissions = listOf(
    USER_READ_SELF,
    USER_READ_ALL_SINGLE,
    USER_READ_ALL_ALL,
    USER_WRITE_SELF,
    USER_WRITE_ALL,
    USER_DELETE,
    USER_BAN
)

private val groupOpsPermissions = listOf(
    GROUP_READ_SINGLE,
    GROUP_READ_ALL,
    GROUP_WRITE,
    GROUP_DELETE
)

private val roleOpsPermissions = listOf(
    ROLE_READ_SELF,
    ROLE_READ_ALL
)

private val permissionOpsPermissions = listOf(
    PERMISSION_READ,
    PERMISSION_WRITE,
    PERMISSION_DELETE
)

private val settingOpsPermissions = listOf(
    SETTING_READ,
    SETTING_WRITE
)

private val strategyOpsPermissions = listOf(
    STRATEGY_READ_ALL,
    STRATEGY_READ_SINGLE,
    STRATEGY_WRITE,
    STRATEGY_DELETE
)

private val imageOpsPermissions = listOf(
    IMAGE_READ_SELF_SINGLE,
    IMAGE_READ_SELF_ALL,
    IMAGE_READ_ALL_SINGLE,
    IMAGE_READ_ALL_ALL,
    IMAGE_WRITE_SELF,
    IMAGE_WRITE_ALL,
    IMAGE_DELETE_SELF,
    IMAGE_DELETE_ALL
)

private val albumOpsPermissions = listOf(
    ALBUM_READ_SELF_SINGLE,
    ALBUM_READ_SELF_ALL,
    ALBUM_READ_ALL_SINGLE,
    ALBUM_READ_ALL_ALL,
    ALBUM_WRITE_SELF,
    ALBUM_WRITE_ALL,
    ALBUM_DELETE_SELF,
    ALBUM_DELETE_ALL
)

private val personalAccessTokenOpsPermissions = listOf(
    PERSONAL_ACCESS_TOKEN_READ_SELF,
    PERSONAL_ACCESS_TOKEN_WRITE_SELF
)
