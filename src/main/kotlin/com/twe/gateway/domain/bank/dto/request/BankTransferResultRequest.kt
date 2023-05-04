package com.tw2.prepaid.domain.bank.dto.request

import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferDetailsRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferRequest
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType

data class BankTransferResultRequest(
    val checkType: OpenbankTransferCheckType,
    val orgBankTranId: String,
    val orgBankTranDate: String,
    val orgTranAmt: Int,
)

fun toObTransferRequest(req: BankTransferResultRequest) = with(req) {
    OpenbankTransferRequest(
        checkType = checkType,
        reqList = listOf(OpenbankTransferDetailsRequest(
            orgBankTranId = orgBankTranId, orgBankTranDate = orgBankTranDate, orgTranAmt = orgTranAmt.toString()
        ))
    )
}
