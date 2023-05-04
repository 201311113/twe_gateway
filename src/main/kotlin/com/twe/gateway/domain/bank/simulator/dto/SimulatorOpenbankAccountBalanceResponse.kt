package com.tw2.prepaid.domain.bank.simulator.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.MILLIS_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankAccountBalanceResponse(
    val apiTranId: String = UUID.randomUUID().toString(),
    val apiTranDtm: String = LocalDateTime.now().format(MILLIS_FORMATTER),
    val rspCode: String,
    val rspMessage: String = EMPTY_MESSAGE,
    val bankTranId: String = EMPTY_MESSAGE,
    val bankTranDate: String = LocalDateTime.now().format(YMD_FORMATTER),
    val bankCodeTran: String = EMPTY_MESSAGE,
    val bankRspCode: String = EMPTY_MESSAGE,
    val bankRspMessage: String = EMPTY_MESSAGE,
    val bankName: String = EMPTY_MESSAGE,
    val fintechUseNum: String = EMPTY_MESSAGE,
    val balanceAmt: String = EMPTY_MESSAGE,             // 계좌잔액(음수가능)
    val availableAmt: String = EMPTY_MESSAGE,           // 출금가능금액
    val accountType: String = EMPTY_MESSAGE,            // 계좌종류 1:수시입출금, 2:예적금 6:수익증권, T:종합계좌
    val productName: String = EMPTY_MESSAGE,            // 상품명
    val accountIssueDate: String = EMPTY_MESSAGE,       // 계좌개설일
    val maturityDate: String = EMPTY_MESSAGE,           // 만기일
    val lastTranDate: String = EMPTY_MESSAGE,           // 최종거래일
)
