package io.sakurasou.model

import io.sakurasou.model.dao.image.Images
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
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}