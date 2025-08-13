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
import org.jetbrains.exposed.sql.Database

/**
 * @author Shiina Kin
 * 2024/9/12 11:48
 */
object InstanceCenter {
    lateinit var client: HttpClient

    lateinit var database: Database

    lateinit var userDao: UserDao
    lateinit var imageDao: ImageDao
    lateinit var albumDao: AlbumDao
    lateinit var strategyDao: StrategyDao
    lateinit var settingDao: SettingDao
    lateinit var groupDao: GroupDao
    lateinit var personalAccessTokenDao: PersonalAccessTokenDao
    lateinit var roleDao: RoleDao
    lateinit var permissionDao: PermissionDao
    lateinit var relationDao: RelationDao

    lateinit var authService: AuthService
    lateinit var userService: UserService
    lateinit var groupService: GroupService
    lateinit var imageService: ImageService
    lateinit var albumService: AlbumService

    lateinit var strategyService: StrategyService
    lateinit var settingService: SettingService
    lateinit var commonService: CommonService
    lateinit var roleService: RoleService
    lateinit var permissionService: PermissionService
    lateinit var personalAccessTokenService: PersonalAccessTokenService

    lateinit var systemService: SystemService
    lateinit var systemStatus: SystemStatus

    fun initClient(
        timeout: Long = 30000,
        proxyAddress: String,
    ) {
        client =
            HttpClient(CIO) {
                install(HttpTimeout) { requestTimeoutMillis = timeout }
                install(Logging)
                if (proxyAddress != "disabled") {
                    engine { proxy = ProxyBuilder.http(proxyAddress) }
                }
            }
    }

    fun initDao() {
        userDao = UserDaoImpl()
        imageDao = ImageDaoImpl()
        albumDao = AlbumDaoImpl()
        strategyDao = StrategyDaoImpl()
        settingDao = SettingDaoImpl()
        groupDao = GroupDaoImpl()
        personalAccessTokenDao = PersonalAccessTokenDaoImpl()
        roleDao = RoleDaoImpl()
        permissionDao = PermissionDaoImpl()
        relationDao = RelationDaoImpl()
    }

    fun initService() {
        settingService = SettingServiceImpl(settingDao)
        permissionService = PermissionServiceImpl(permissionDao)

        authService = AuthServiceImpl(userDao, relationDao)
        strategyService = StrategyServiceImpl(strategyDao, groupDao)

        albumService = AlbumServiceImpl(userDao, albumDao, imageDao)
        roleService = RoleServiceImpl(roleDao, permissionDao, relationDao)
        systemService = SystemServiceImpl(imageDao, albumDao, userDao)

        groupService =
            GroupServiceImpl(
                groupDao,
                userDao,
                strategyDao,
                relationDao,
            )
        personalAccessTokenService =
            PersonalAccessTokenServiceImpl(
                personalAccessTokenDao,
                userDao,
                groupDao,
                relationDao,
            )

        userService =
            UserServiceImpl(
                userDao,
                groupDao,
                albumDao,
                imageDao,
                settingService,
            )
        commonService =
            CommonServiceImpl(
                userDao,
                albumDao,
                strategyDao,
                imageDao,
                settingService,
            )

        imageService =
            ImageServiceImpl(
                imageDao,
                albumDao,
                userDao,
                groupDao,
                strategyDao,
                settingService,
            )
    }

    suspend fun initSystemStatus() {
        systemStatus = settingService.getSystemStatus()
    }
}
