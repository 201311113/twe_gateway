package com.tw2.prepaid.common.properties

enum class Profile(
    val isLocal: Boolean = false
) {
    TEST(isLocal = true), LOCAL(isLocal = true), DEV, PROD;
}