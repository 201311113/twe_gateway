package com.tw2.prepaid.domain.bank.dto.response

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.error.ErrorCodeHolder
import com.tw2.prepaid.domain.bank.model.OpenBankApiResponseCode
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankDepositTargetResponse
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankDepositResponse

data class BankDepositResponse(
    val rspCode: String = OpenBankApiResponseCode.A0000.name,
    val errorCodeHolder: ErrorCodeHolder = ErrorCodeHolder(ErrorCode.SUCCESS),
    val wdBankCodeStd: String = EMPTY_MESSAGE,
    val wdBankName: String = EMPTY_MESSAGE,
    val resList: List<DepositTargetResponse> = emptyList()
)
fun of(response: OpenbankDepositResponse, errorCodeHolder: ErrorCodeHolder) = with(response) {
    BankDepositResponse(
        rspCode = rspCode,
        wdBankCodeStd = wdBankCodeStd,
        wdBankName = wdBankName,
        errorCodeHolder = errorCodeHolder,
        resList = resList.map(::create).toList()
    )
}
data class DepositTargetResponse(
    val bankCodeStd: String,
    val bankName: String,
    val tranAmt: Long,
    val fintechUseNum: String,
    val bankRspCode: String,
)
private fun create(response: OpenbankDepositTargetResponse) = with(response) {
    DepositTargetResponse(
        bankCodeStd = bankCodeStd,
        bankName = bankName,
        tranAmt = tranAmt.toLong(),
        fintechUseNum = fintechUseNum,
        bankRspCode = bankRspCode,
    )
}