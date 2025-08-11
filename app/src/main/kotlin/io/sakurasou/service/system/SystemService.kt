package io.sakurasou.service.system

import io.sakurasou.controller.vo.SystemOverviewVO
import io.sakurasou.controller.vo.SystemStatisticsVO

/**
 * @author Shiina Kin
 * 2024/11/18 20:17
 */
interface SystemService {
    suspend fun fetchSystemStatistics(): SystemStatisticsVO

    suspend fun fetchSystemOverview(): SystemOverviewVO
}
