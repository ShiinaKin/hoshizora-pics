package io.sakurasou.hoshizora.model.dao.relation

import io.sakurasou.hoshizora.model.dao.permission.Permissions
import io.sakurasou.hoshizora.model.dao.personalAccessToken.PersonalAccessTokens
import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/11/15 15:08
 */
object PersonalAccessTokenPermissions : Table("personal_access_token_permissions") {
    val tokenId = long("token_id")
    val permission = varchar("permission", 255)

    override val primaryKey = PrimaryKey(tokenId, permission)

    init {
        foreignKey(tokenId to PersonalAccessTokens.id)
        foreignKey(permission to Permissions.name)
    }
}
