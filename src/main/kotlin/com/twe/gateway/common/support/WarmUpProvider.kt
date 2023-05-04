package com.tw2.prepaid.common.support

import com.fasterxml.jackson.databind.node.ObjectNode
import com.tw2.prepaid.common.properties.PrepaidProperties
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

private val log = KotlinLogging.logger {}
@Component
class WarmUpProvider(private val pp: PrepaidProperties): ApplicationListener<ApplicationStartedEvent> {
    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val warmUpInfo = pp.warmUp
        if (!warmUpInfo.enabled) return

        val restTemplate = RestTemplate()
        log.info("warm-up started.")
        (1..warmUpInfo.count).forEach {
            val response = restTemplate.exchange(warmUpInfo.url, HttpMethod.GET, null, ObjectNode::class.java)
            log.info(response.toString())
            Thread.sleep(1)
        }
    }
}