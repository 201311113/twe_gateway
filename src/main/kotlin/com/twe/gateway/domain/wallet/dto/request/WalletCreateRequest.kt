package com.tw2.prepaid.domain.wallet.dto.request
data class WalletCreateRequest(
    //val partnerId: Long,
    //val userId: Long,
    val userId: Long,
    // @Schema(description = "TODO 얘는 어떻게 받을지 고민이 필요하다, 계좌 생성시에 나오는 놈이라 member 정보로 보는게 속편할지..")
    // val userSeqNum: String,
)
