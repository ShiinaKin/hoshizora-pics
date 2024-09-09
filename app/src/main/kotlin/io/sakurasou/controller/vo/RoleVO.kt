package io.sakurasou.controller.vo

import io.sakurasou.model.entity.Permission

/**
 * @author Shiina Kin
 * 2024/9/9 10:45
 */
data class RoleVO(
    val id: Long,
    val name: String,
    val description: String? = null,
    val permissions: List<Permission>
)