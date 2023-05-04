package com.tw2.prepaid.domain.bank.dto.request.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankTransferRequest(
    val checkType: OpenbankTransferCheckType,
    val tranDtime: String = makeTranDtime(),
    val reqList: List<OpenbankTransferDetailsRequest>,
    val reqCnt: String = reqList.size.toString()
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankTransferDetailsRequest(
    val tranNo: String = "1", // list 내의 일련번호인듯?
    val orgBankTranId: String,
    val orgBankTranDate: String,
    val orgTranAmt: String
)
