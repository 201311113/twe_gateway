package com.tw2.prepaid.domain.bank.simulator.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankTransferRequest(
    val checkType: OpenbankTransferCheckType,
    val tranDtime: String = makeTranDtime(),
    val reqList: List<SimulatorOpenbankTransferDetailsRequest>,
    val reqCnt: String = reqList.size.toString()
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankTransferDetailsRequest(
    val tranNo: String = "1", // list 내의 일련번호인듯?
    val orgBankTranId: String,
    val orgBankTranDate: String,
    val orgTranAmt: String
)
