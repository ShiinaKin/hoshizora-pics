package io.sakurasou.model.common

import io.sakurasou.config.InstanceCenter
import io.sakurasou.constant.*
import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.permission.Permissions
import io.sakurasou.model.dao.relation.GroupRoles
import io.sakurasou.model.dao.relation.RolePermissions
import io.sakurasou.model.dao.role.Roles
import io.sakurasou.model.dao.setting.Settings
import io.sakurasou.model.dao.strategy.Strategies
import io.sakurasou.model.dao.user.Users
import io.sakurasou.model.dto.*
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.StrategySetting
import io.sakurasou.model.setting.SystemSetting
import io.sakurasou.model.setting.SystemStatus
import io.sakurasou.model.strategy.LocalStrategy
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exposedLogger

/**
 * @author Shiina Kin
 * 2024/9/12 16:22
 */
fun init() {
    InstanceCenter.initDao()
    if (isFirstRunning()) return
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
    initStrategy()
    initSetting()
    initPermission()
    initRole()
    initGroup()
    initRelation()
}

private fun isFirstRunning(): Boolean {
    return SchemaUtils.listTables().size == 10
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

private fun initPermission() {
    val allPermissions = listOf(
        userOpsPermissions,
        groupOpsPermissions,
        roleOpsPermissions,
        permissionOpsPermissions,
        settingOpsPermissions,
        strategyOpsPermissions,
        imageOpsPermissions,
        albumOpsPermissions
    )
        .flatten()
        .map { PermissionInsertDTO(it, null) }

    InstanceCenter.permissionDao.batchSavePermission(allPermissions)

    exposedLogger.info("permission init success")
}

private fun initRole() {
    val adminRoleInsertDTO = RoleInsertDTO(ROLE_ADMIN, null)
    val userRoleInsertDTO = RoleInsertDTO(ROLE_USER, null)
    InstanceCenter.roleDao.saveRole(adminRoleInsertDTO)
    InstanceCenter.roleDao.saveRole(userRoleInsertDTO)

    exposedLogger.info("role init success")
}

private fun initStrategy() {
    val localStrategyConfig = LocalStrategy("/")
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val strategyInsertDTO = StrategyInsertDTO(
        name = "local",
        config = localStrategyConfig,
        createTime = now,
        updateTime = now
    )

    InstanceCenter.strategyDao.saveStrategy(strategyInsertDTO)
    exposedLogger.info("strategy init success")
}

private fun initGroup() {
    val adminGroup = GroupInsertDTO(GROUP_ADMIN, "admin group", 1)
    val userGroup = GroupInsertDTO(GROUP_USER, "user group", 1)
    InstanceCenter.groupDao.saveGroup(adminGroup)
    InstanceCenter.groupDao.saveGroup(userGroup)

    exposedLogger.info("group init success")
}

private fun initRelation() {
    // role-permission
    val adminPermissions = listOf(
        userOpsPermissions,
        groupOpsPermissions,
        settingOpsPermissions,
        strategyOpsPermissions,
        imageOpsPermissions,
        albumOpsPermissions
    ).flatten()
    val userPermissions = listOf(
        USER_READ_SELF,
        USER_WRITE_SELF,
        IMAGE_READ_SELF_SINGLE,
        IMAGE_READ_SELF_ALL,
        IMAGE_WRITE_SELF,
        IMAGE_DELETE_SELF,
        ALBUM_READ_SELF_SINGLE,
        ALBUM_READ_SELF_ALL,
        ALBUM_WRITE_SELF,
        ALBUM_DELETE_SELF
    )

    InstanceCenter.relationDao.batchInsertRoleToPermissions(ROLE_ADMIN, adminPermissions)
    InstanceCenter.relationDao.batchInsertRoleToPermissions(ROLE_USER, userPermissions)

    exposedLogger.info("role-permission relation init success")

    // group-role
    val adminRoles = listOf(ROLE_ADMIN)
    val userRoles = listOf(ROLE_USER)
    InstanceCenter.relationDao.batchInsertGroupToRoles(1, adminRoles)
    InstanceCenter.relationDao.batchInsertGroupToRoles(2, userRoles)

    exposedLogger.info("group-role relation init success")
}

private fun initSetting() {
    val siteSettingConfig = SiteSetting(
        siteTitle = "HoshizoraPics",
        siteSubtitle = "A simple pic management",
        siteKeyword = "pic, management",
        siteDescription = "A simple pic management",
        homePageRandomPicDisplay = false
    )
    val strategySettingConfig = StrategySetting(
        allowedImageTypes = listOf("jpg", "jpeg", "png", "gif"),
    )
    val systemSettingConfig = SystemSetting(
        defaultGroupId = 1,
        allowSignup = false
    )
    val systemStatus = SystemStatus(
        isInit = false
    )

    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val siteSettingInsertDTO = SettingInsertDTO(SETTING_SITE, siteSettingConfig, now, now)
    val strategySettingInsertDTO = SettingInsertDTO(SETTING_STRATEGY, strategySettingConfig, now, now)
    val systemSettingInsertDTO = SettingInsertDTO(SETTING_SYSTEM, systemSettingConfig, now, now)
    val systemStatusInsertDTO = SettingInsertDTO(SETTING_STATUS, systemStatus, now, now)

    InstanceCenter.settingDao.saveSetting(siteSettingInsertDTO)
    InstanceCenter.settingDao.saveSetting(strategySettingInsertDTO)
    InstanceCenter.settingDao.saveSetting(systemSettingInsertDTO)
    InstanceCenter.settingDao.saveSetting(systemStatusInsertDTO)

    exposedLogger.info("setting init success")
}
