package io.sakurasou.model

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.sakurasou.di.InstanceCenter
import io.sakurasou.model.common.DatabaseInit
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * @author ShiinaKin
 * 2024/9/5 14:33
 */
object DatabaseSingleton {
    fun init(
        jdbcURL: String,
        driverClassName: String,
        username: String,
        password: String,
        version: String,
    ) {
        val hikariConfig =
            HikariConfig().apply {
                jdbcUrl = jdbcURL
                this.driverClassName = driverClassName
                this.username = username
                this.password = password
                maximumPoolSize = 5
                isReadOnly = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            }
        val dataSource = HikariDataSource(hikariConfig)

        InstanceCenter.database = Database.connect(dataSource)

        DatabaseInit.init(version)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO, InstanceCenter.database) { block() }
}
