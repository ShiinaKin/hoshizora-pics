package io.sakurasou.config

import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.DatabaseSingleton.dbQueryInner
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.album.AlbumDaoImpl
import io.sakurasou.model.dao.group.GroupDao
import io.sakurasou.model.dao.group.GroupDaoImpl
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.image.ImageDaoImpl
import io.sakurasou.model.dao.permission.PermissionDao
import io.sakurasou.model.dao.permission.PermissionDaoImpl
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.relation.RelationDaoImpl
import io.sakurasou.model.dao.role.RoleDao
import io.sakurasou.model.dao.role.RoleDaoImpl
import io.sakurasou.model.dao.setting.SettingDao
import io.sakurasou.model.dao.setting.SettingDaoImpl
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dao.strategy.StrategyDaoImpl
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dao.user.UserDaoImpl
import io.sakurasou.model.setting.SystemStatus
import io.sakurasou.service.album.AlbumService
import io.sakurasou.service.album.AlbumServiceImpl
import io.sakurasou.service.auth.AuthService
import io.sakurasou.service.auth.AuthServiceImpl
import io.sakurasou.service.common.CommonService
import io.sakurasou.service.common.CommonServiceImpl
import io.sakurasou.service.image.ImageService
import io.sakurasou.service.role.RoleService
import io.sakurasou.service.role.RoleServiceImpl
import io.sakurasou.service.setting.SettingService
import io.sakurasou.service.setting.SettingServiceImpl
import io.sakurasou.service.strategy.StrategyService
import io.sakurasou.service.strategy.StrategyServiceImpl
import io.sakurasou.service.user.UserService
import io.sakurasou.service.user.UserServiceImpl
import kotlinx.coroutines.runBlocking

/**
 * @author Shiina Kin
 * 2024/9/12 11:48
 */
object InstanceCenter {
    lateinit var userDao: UserDao
    lateinit var imageDao: ImageDao
    lateinit var albumDao: AlbumDao
    lateinit var strategyDao: StrategyDao
    lateinit var settingDao: SettingDao
    lateinit var groupDao: GroupDao
    lateinit var roleDao: RoleDao
    lateinit var permissionDao: PermissionDao
    lateinit var relationDao: RelationDao

    lateinit var authService: AuthService
    lateinit var userService: UserService
    lateinit var imageService: ImageService
    lateinit var albumService: AlbumService

    lateinit var strategyService: StrategyService
    lateinit var settingService: SettingService
    lateinit var commonService: CommonService
    lateinit var roleService: RoleService
    // lateinit var permissionService: UserService
    // lateinit var relationService: UserService

    lateinit var systemStatus: SystemStatus
    lateinit var rolePermissions: Map<String, Set<String>>

    fun initDao() {
        userDao = UserDaoImpl()
        imageDao = ImageDaoImpl()
        albumDao = AlbumDaoImpl()
        strategyDao = StrategyDaoImpl()
        settingDao = SettingDaoImpl()
        groupDao = GroupDaoImpl()
        roleDao = RoleDaoImpl()
        permissionDao = PermissionDaoImpl()
        relationDao = RelationDaoImpl()
    }

    fun initService() {
        albumService = AlbumServiceImpl(albumDao)
        settingService = SettingServiceImpl(settingDao)
        strategyService = StrategyServiceImpl(strategyDao)
        authService = AuthServiceImpl(userDao, relationDao)

        roleService = RoleServiceImpl(roleDao, permissionDao, relationDao)
        userService = UserServiceImpl(userDao, albumService, settingService)
        commonService = CommonServiceImpl(userDao, albumService, settingService)
    }

    fun initSystemStatus() {
        systemStatus = runBlocking {
            settingService.getSystemStatus()
        }
    }

    fun initRolePermissions() {
        rolePermissions = runBlocking {
            dbQuery {
                val roles = dbQueryInner { roleDao.listRoles() }
                roles.associate {
                    it.name to dbQueryInner { relationDao.listPermissionByRole(it.name).toSet() }
                }
            }
        }
    }
}