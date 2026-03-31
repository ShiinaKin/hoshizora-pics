package io.sakurasou.hoshizora.model

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.sakurasou.hoshizora.di.InstanceCenter
import io.sakurasou.hoshizora.model.common.DatabaseInit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

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

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        suspendTransaction(InstanceCenter.database) {
            withContext(Dispatchers.IO) {
                block()
            }
        }
}
