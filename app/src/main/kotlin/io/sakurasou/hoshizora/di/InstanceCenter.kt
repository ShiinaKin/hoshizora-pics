package io.sakurasou.hoshizora.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.http
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.Logging
import io.sakurasou.hoshizora.model.dao.album.AlbumDao
import io.sakurasou.hoshizora.model.dao.album.AlbumDaoImpl
import io.sakurasou.hoshizora.model.dao.group.GroupDao
import io.sakurasou.hoshizora.model.dao.group.GroupDaoImpl
import io.sakurasou.hoshizora.model.dao.image.ImageDao
import io.sakurasou.hoshizora.model.dao.image.ImageDaoImpl
import io.sakurasou.hoshizora.model.dao.permission.PermissionDao
import io.sakurasou.hoshizora.model.dao.permission.PermissionDaoImpl
import io.sakurasou.hoshizora.model.dao.personalAccessToken.PersonalAccessTokenDao
import io.sakurasou.hoshizora.model.dao.personalAccessToken.PersonalAccessTokenDaoImpl
import io.sakurasou.hoshizora.model.dao.relation.RelationDao
import io.sakurasou.hoshizora.model.dao.relation.RelationDaoImpl
import io.sakurasou.hoshizora.model.dao.role.RoleDao
import io.sakurasou.hoshizora.model.dao.role.RoleDaoImpl
import io.sakurasou.hoshizora.model.dao.setting.SettingDao
import io.sakurasou.hoshizora.model.dao.setting.SettingDaoImpl
import io.sakurasou.hoshizora.model.dao.strategy.StrategyDao
import io.sakurasou.hoshizora.model.dao.strategy.StrategyDaoImpl
import io.sakurasou.hoshizora.model.dao.task.TaskDao
import io.sakurasou.hoshizora.model.dao.task.TaskDaoImpl
import io.sakurasou.hoshizora.model.dao.user.UserDao
import io.sakurasou.hoshizora.model.dao.user.UserDaoImpl
import io.sakurasou.hoshizora.model.setting.SystemStatus
import io.sakurasou.hoshizora.service.album.AlbumService
import io.sakurasou.hoshizora.service.album.AlbumServiceImpl
import io.sakurasou.hoshizora.service.auth.AuthService
import io.sakurasou.hoshizora.service.auth.AuthServiceImpl
import io.sakurasou.hoshizora.service.common.CommonService
import io.sakurasou.hoshizora.service.common.CommonServiceImpl
import io.sakurasou.hoshizora.service.group.GroupService
import io.sakurasou.hoshizora.service.group.GroupServiceImpl
import io.sakurasou.hoshizora.service.image.ImageService
import io.sakurasou.hoshizora.service.image.ImageServiceImpl
import io.sakurasou.hoshizora.service.permission.PermissionService
import io.sakurasou.hoshizora.service.permission.PermissionServiceImpl
import io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService
import io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenServiceImpl
import io.sakurasou.hoshizora.service.role.RoleService
import io.sakurasou.hoshizora.service.role.RoleServiceImpl
import io.sakurasou.hoshizora.service.setting.SettingService
import io.sakurasou.hoshizora.service.setting.SettingServiceImpl
import io.sakurasou.hoshizora.service.strategy.StrategyService
import io.sakurasou.hoshizora.service.strategy.StrategyServiceImpl
import io.sakurasou.hoshizora.service.system.SystemService
import io.sakurasou.hoshizora.service.system.SystemServiceImpl
import io.sakurasou.hoshizora.service.user.UserService
import io.sakurasou.hoshizora.service.user.UserServiceImpl

/**
 * @author Shiina Kin
 * 2024/9/12 11:48
 */
object InstanceCenter {
    lateinit var systemStatus: SystemStatus

    fun init() {
        diOperation {
            register<UserDao> { UserDaoImpl() }
            register<ImageDao> { ImageDaoImpl() }
            register<AlbumDao> { AlbumDaoImpl() }
            register<StrategyDao> { StrategyDaoImpl() }
            register<SettingDao> { SettingDaoImpl() }
            register<GroupDao> { GroupDaoImpl() }
            register<PersonalAccessTokenDao> { PersonalAccessTokenDaoImpl() }
            register<RoleDao> { RoleDaoImpl() }
            register<PermissionDao> { PermissionDaoImpl() }
            register<RelationDao> { RelationDaoImpl() }
            register<TaskDao> { TaskDaoImpl() }
            register<AuthService> { AuthServiceImpl(get(), get()) }
            register<UserService> { UserServiceImpl(get(), get(), get(), get(), get()) }
            register<GroupService> { GroupServiceImpl(get(), get(), get(), get()) }
            register<ImageService> { ImageServiceImpl(get(), get(), get(), get(), get(), get()) }
            register<AlbumService> { AlbumServiceImpl(get(), get(), get()) }
            register<StrategyService> { StrategyServiceImpl(get(), get()) }
            register<SettingService> { SettingServiceImpl(get()) }
            register<CommonService> { CommonServiceImpl(get(), get(), get(), get(), get()) }
            register<RoleService> { RoleServiceImpl(get(), get(), get()) }
            register<PermissionService> { PermissionServiceImpl(get()) }
            register<PersonalAccessTokenService> { PersonalAccessTokenServiceImpl(get(), get(), get(), get()) }
            register<SystemService> { SystemServiceImpl(get(), get(), get()) }
        }
    }

    fun initClient(
        timeout: Long = 30000,
        proxyAddress: String,
    ) {
        diOperation {
            register {
                HttpClient(CIO) {
                    install(HttpTimeout) { requestTimeoutMillis = timeout }
                    install(Logging)
                    if (proxyAddress != "disabled") {
                        engine { proxy = ProxyBuilder.http(proxyAddress) }
                    }
                }
            }
        }
    }

    suspend fun initSystemStatus() {
        val settingService: SettingService by inject()
        systemStatus = settingService.getSystemStatus()
    }
}
