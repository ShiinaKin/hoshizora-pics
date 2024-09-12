package io.sakurasou.config

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
}