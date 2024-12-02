package io.sakurasou.model.dto

/**
 * @author Shiina Kin
 * 2024/9/12 16:25
 */
data class RoleInsertDTO(
    val name: String,
    val displayName: String,
    val isSystemReserved: Boolean,
    val description: String?
)

data class RoleUpdateDTO(
    val displayName: String,
    val description: String?
)