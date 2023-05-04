package com.tw2.prepaid.common.utils

import com.tw2.prepaid.common.support.ApplicationContextProvider

fun <T> getBean(name: String, type: Class<T>) =
    ApplicationContextProvider.ac.getBean(name, type)
fun <T> getBean(type: Class<T>) =
    ApplicationContextProvider.ac.getBean(type)