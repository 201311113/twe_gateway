package com.tw2.prepaid.domain.bank.dto.response

import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankAccountBalanceResponse

data class BankBalanceResponse(
    val bankName: String,
    val balanceAmount: Long,
    val availableAmount: Long
)
fun create(response: OpenbankAccountBalanceResponse): BankBalanceResponse {
    return BankBalanceResponse(
        bankName = response.bankName,
        balanceAmount = response.balanceAmt.toLong(),
        availableAmount = response.availableAmt.toLong()
    )
}
