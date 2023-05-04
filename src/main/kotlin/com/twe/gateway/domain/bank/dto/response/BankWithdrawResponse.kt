package com.tw2.prepaid.domain.bank.dto.response

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.error.ErrorCodeHolder
import com.tw2.prepaid.domain.bank.model.OpenBankApiResponseCode
import com.tw2.prepaid.domain.bank.model.ParticipatingBankResponseCode
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankWithdrawResponse

data class BankWithdrawResponse(
    val rspCode: String = OpenBankApiResponseCode.A0000.name,
    val bankRspCode: String = ParticipatingBankResponseCode.`000`.name,
    val rspMessage: String = EMPTY_MESSAGE,
    val bankRspMessage: String = EMPTY_MESSAGE,
    val errorCodeHolder: ErrorCodeHolder = ErrorCodeHolder(ErrorCode.SUCCESS),
    val tranAmt: String = EMPTY_MESSAGE,
    val apiTranId: String = EMPTY_MESSAGE,
    val apiTranDtm: String = EMPTY_MESSAGE,
)
fun of(response: OpenbankWithdrawResponse, errorCodeHolder: ErrorCodeHolder) = with(response) {
    BankWithdrawResponse(
        rspCode = rspCode, bankRspCode = bankRspCode, rspMessage = rspMessage,
        errorCodeHolder = errorCodeHolder, bankRspMessage = bankRspMessage,
        tranAmt = tranAmt, apiTranId = apiTranId, apiTranDtm = apiTranDtm
    )
}