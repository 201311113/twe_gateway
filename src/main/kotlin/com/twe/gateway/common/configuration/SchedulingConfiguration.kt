package com.tw2.prepaid.common.configuration

import com.tw2.prepaid.domain.bank.service.OpenbankService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@EnableScheduling
@Configuration
@ConditionalOnProperty(
    value = ["app.scheduling.enabled"], havingValue = "true", matchIfMissing = true
)
class SchedulingConfiguration(
    private val obService: OpenbankService,
) {
    @Scheduled(fixedDelay = 60 * 1000)
    fun obRefreshToken() = obService.refreshAccessToken()
}