package com.tw2.prepaid.common.support.annotation
// package 변경을 함부로 하면 안된다.
// see CustomAnnotationAdvisor
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrimaryDb
