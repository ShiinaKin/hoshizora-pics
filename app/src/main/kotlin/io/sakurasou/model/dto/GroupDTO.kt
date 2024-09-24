package io.sakurasou.model.dto

/**
 * @author Shiina Kin
 * 2024/9/12 13:46
 */
data class GroupInsertDTO(
    val name: String,
    val description: String?,
    val strategyId: Long,
    val maxSize: Long = 5 * 1024 * 1024 * 1024L
)

data class GroupUpdateDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val strategyId: Long,
    val maxSize: Long
)