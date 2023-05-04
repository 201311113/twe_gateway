package com.tw2.prepaid.domain.bank.simulator.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.MILLIS_FORMATTER
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankTransferResponse(
    val apiTranId: String,
    val apiTranDtm: String = LocalDateTime.now().format(MILLIS_FORMATTER),
    val rspCode: String,
    val rspMessage: String = EMPTY_MESSAGE,
    val resCnt: String,
    val resList: List<SimulatorOpenbankTransferDetailsResponse>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankTransferDetailsResponse(
    val tranNo: String = EMPTY_MESSAGE,
    val bankTranId: String = EMPTY_MESSAGE,
    val bankTranDate: String = EMPTY_MESSAGE,
    val bankCodeTran: String = EMPTY_MESSAGE, // 무슨 의미인지 잘 모름 딱히 사용은 안하는 듯.
    val bankRspCode: String,
    val bankRspMessage: String = EMPTY_MESSAGE,
    val wdBankCodeStd: String = EMPTY_MESSAGE,
    val wdBankCodeSub: String = EMPTY_MESSAGE,
    val wdBankName: String = EMPTY_MESSAGE,
    val wdSavingsBankName: String = EMPTY_MESSAGE,
    val wdFintechUseNum: String = EMPTY_MESSAGE,
    val wdAccountNumMasked: String = EMPTY_MESSAGE,
    val wdPrintContent: String = EMPTY_MESSAGE,
    val wdAccountHolderName: String = EMPTY_MESSAGE,
    val dpsBankCodeStd: String = EMPTY_MESSAGE,
    val dpsBankCodeSub: String = EMPTY_MESSAGE,
    val dpsBankName: String = EMPTY_MESSAGE,
    val dpsSavingsBankName: String = EMPTY_MESSAGE,
    val dpsFintechUseNum: String = EMPTY_MESSAGE,
    val dpsAccountNumMasked: String = EMPTY_MESSAGE,
    val dpsPrintContent: String = EMPTY_MESSAGE,
    val dpsAccountHolderName: String = EMPTY_MESSAGE,
    val tranAmt: String = EMPTY_MESSAGE
)
