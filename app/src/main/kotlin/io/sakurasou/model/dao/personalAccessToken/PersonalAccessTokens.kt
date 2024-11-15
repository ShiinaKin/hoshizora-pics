package io.sakurasou.model.dao.personalAccessToken

import io.sakurasou.model.dao.user.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/11/14 22:09
 */
object PersonalAccessTokens: LongIdTable("personal_access_tokens") {
    val userId = long("user_id")
    val token = char("token", 60)
    val name = varchar("name", 255).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val createTime = datetime("create_time")
    val expireTime = datetime("expire_time")

    init {
        foreignKey(userId to Users.id)
    }

    val columnMap = mapOf(
        "createTime" to createTime,
        "expireTime" to expireTime
    )
}