package com.tw2.prepaid.domain.bank.model

enum class OpenbankTransferResultEnum {
    PROCESSING,
    RETRY_DONE,
    DONE,
    RETRY_FAILED,
    READ_TIMEOUT,
}