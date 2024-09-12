package io.sakurasou.model

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.sakurasou.model.common.init
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author ShiinaKin
 * 2024/9/5 14:33
 */
object DatabaseSingleton {
    fun init(jdbcURL: String, driverClassName: String, username: String, password: String) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = jdbcURL
            this.driverClassName = driverClassName
            this.username = username
            this.password = password
            maximumPoolSize = 5
            isReadOnly = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
        }
        val dataSource = HikariDataSource(hikariConfig)

        val database = Database.connect(dataSource)

        transaction(database) {
            init()
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}