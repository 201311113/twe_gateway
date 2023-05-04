package com.tw2.prepaid.common.support.aop

import com.tw2.prepaid.common.configuration.PRIMARY_DB
import com.tw2.prepaid.common.utils.setRequestAttribute
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Aspect
@Component
class CustomAnnotationAdvisor { // 스프링 내부적으로 쓰이기 때문에 클래스 지우면 안된다.
    @Before("@annotation(com.tw2.prepaid.common.support.annotation.PrimaryDb)")
    fun doAtAnnotation(joinPoint: JoinPoint) {
        setRequestAttribute(PRIMARY_DB, true)
    }
}