package io.sakurasou.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.apache.commons.codec.digest.DigestUtils
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @author Shiina Kin
 * 2024/10/12 10:22
 */
object PlaceholderUtils {
    /**
     * {yyyy}	        年(2000)
     * {MM}	            月(09)
     * {dd}	            日(23)
     * {timestamp}	    时间戳(秒)
     * {uniq}	        唯一字符串
     * {md5}	        随机 md5 值
     * {str-random-16}	16位随机字符串
     * {str-random-10}	10位随机字符串
     * {fileName}	    文件原始名称
     * {user-id}	    用户 ID
     */
    private enum class Placeholder(
        val placeholder: String,
    ) {
        YEAR("{yyyy}"),
        MONTH("{MM}"),
        DAY("{dd}"),
        TIMESTAMP("{timestamp}"),
        UNIQ("{uniq}"),
        MD5("{md5}"),
        STR_RANDOM_16("{str-random-16}"),
        STR_RANDOM_10("{str-random-10}"),
        FILENAME("{filename}"),
        USER_ID("{user-id}"),
    }

    @OptIn(ExperimentalUuidApi::class)
    @Synchronized
    fun parsePlaceholder(
        namingRule: String,
        fileName: String,
        userId: Long,
    ): String {
        val instant = Clock.System.now()
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

        // namingRule like : {yyyy}/{MM}/{dd}
        return namingRule.replace(Regex("\\{[^}]+}")) { matchResult ->
            when (matchResult.value) {
                Placeholder.YEAR.placeholder -> localDateTime.year.toString()
                Placeholder.MONTH.placeholder -> localDateTime.monthNumber.toString().padStart(2, '0')
                Placeholder.DAY.placeholder -> localDateTime.dayOfMonth.toString().padStart(2, '0')
                Placeholder.TIMESTAMP.placeholder -> instant.epochSeconds.toString()
                Placeholder.UNIQ.placeholder -> Uuid.random().toHexString()
                Placeholder.MD5.placeholder -> DigestUtils.md5Hex(generateRandomString(32))
                Placeholder.STR_RANDOM_16.placeholder -> generateRandomString(16)
                Placeholder.STR_RANDOM_10.placeholder -> generateRandomString(10)
                Placeholder.FILENAME.placeholder -> fileName
                Placeholder.USER_ID.placeholder -> userId.toString()
                else -> matchResult.value
            }
        }
    }

    private fun generateRandomString(length: Int): String {
        val dict: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random.nextInt(0, dict.size) }
            .map(dict::get)
            .joinToString("")
    }
}
