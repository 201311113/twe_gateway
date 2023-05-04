package com.tw2.prepaid.common.support

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ApplicationContextProvider: ApplicationContextAware {
    companion object {
        lateinit var ac: ApplicationContext
            private set
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ac = applicationContext
    }
}