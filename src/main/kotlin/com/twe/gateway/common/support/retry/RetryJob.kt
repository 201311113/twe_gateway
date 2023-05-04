package com.tw2.prepaid.common.support.retry

data class RetryJob (
    val retryJobType: RetryJobType,
    val data: String,
)

enum class RetryJobType {
    OB_WITHDRAW, // 오픈뱅킹 출금
    OB_DEPOSIT, // 오픈뱅킹 입금
}