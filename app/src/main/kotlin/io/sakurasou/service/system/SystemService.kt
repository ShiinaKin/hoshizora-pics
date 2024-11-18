package io.sakurasou.service.system

import io.sakurasou.controller.vo.SystemOverviewVO

/**
 * @author Shiina Kin
 * 2024/11/18 20:17
 */
interface SystemService {
    suspend fun fetchSystemOverview(): SystemOverviewVO
}