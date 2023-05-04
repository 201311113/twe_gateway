package com.tw2.prepaid.common.utils

import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

fun setRequestAttribute(key: String, value: Any) =
    RequestContextHolder.getRequestAttributes()?.setAttribute(key, value, RequestAttributes.SCOPE_REQUEST)
fun getRequestAttribute(key: String): Any? =
    RequestContextHolder.getRequestAttributes()?.getAttribute(key, RequestAttributes.SCOPE_REQUEST)