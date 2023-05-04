package com.tw2.prepaid.domain.bank.dto.response

import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankTransferDetailsResponse
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankTransferResponse

data class BankTransferResponse(
    val rspCode: String,
    val errorCode: ErrorCode,
    val resList: List<BankTransferTargetResponse>,
)
fun of(response: OpenbankTransferResponse, errorCode: ErrorCode) = with(response) {
    BankTransferResponse(
        rspCode = rspCode,
        errorCode = errorCode,
        resList = resList.map(::create).toList()
    )
}
data class BankTransferTargetResponse(
    val bankRspCode: String,
    val bankRspMessage: String,
    val wdBankCodeStd: String,
    val wdBankName: String,
    val wdAccountHolderName: String,
    val wdAccountNumMasked: String,
    val dpsAccountHolderName: String,
    val dpsAccountNumMasked: String,
    val tranAmt: Long
)
fun create(response: OpenbankTransferDetailsResponse) = with(response) {
    BankTransferTargetResponse(
        bankRspCode = bankRspCode,
        bankRspMessage = bankRspMessage,
        wdBankCodeStd = wdBankCodeStd,
        wdBankName = wdBankName,
        wdAccountHolderName = wdAccountHolderName,
        wdAccountNumMasked = wdAccountNumMasked,
        dpsAccountHolderName = dpsAccountHolderName,
        dpsAccountNumMasked = dpsAccountNumMasked,
        tranAmt = tranAmt.toLong()
    )
}
