package io.sakurasou.model

import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.permission.Permissions
import io.sakurasou.model.dao.relation.GroupRoles
import io.sakurasou.model.dao.relation.RolePermissions
import io.sakurasou.model.dao.relation.UserGroups
import io.sakurasou.model.dao.role.Roles
import io.sakurasou.model.dao.setting.Settings
import io.sakurasou.model.dao.strategy.Strategies
import io.sakurasou.model.dao.user.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author ShiinaKin
 * 2024/9/5 14:33
 */
object DatabaseSingleton {
    fun init(jdbcURL: String, driverClassName: String, username: String, password: String) {
        val database = Database.connect(
            url = jdbcURL,
            driver = driverClassName,
            user = username,
            password = password
        )
        transaction(database) {
            SchemaUtils.create(Images)
            SchemaUtils.create(Albums)
            SchemaUtils.create(Strategies)
            SchemaUtils.create(Settings)
            SchemaUtils.create(Users)
            SchemaUtils.create(Groups)
            SchemaUtils.create(Roles)
            SchemaUtils.create(Permissions)
            SchemaUtils.create(UserGroups)
            SchemaUtils.create(GroupRoles)
            SchemaUtils.create(RolePermissions)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}