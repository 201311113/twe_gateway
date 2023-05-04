package com.tw2.prepaid.domain.bank.dto.response.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.MILLIS_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import com.tw2.prepaid.domain.bank.model.OpenbankAccountType
import com.tw2.prepaid.domain.bank.model.OpenbankCntrAccountType
import com.tw2.prepaid.domain.bank.model.entity.OpenbankAccount
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankRegisterAccountResponse(
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
    val savingsBankName: String = EMPTY_MESSAGE,
    val userSeqNo: String = EMPTY_MESSAGE,
    val fintechUseNum: String = EMPTY_MESSAGE,
    val payerNum: String = EMPTY_MESSAGE,
    val accountType: OpenbankAccountType = OpenbankAccountType.수시입출금,
    val transferBankTranId: String = EMPTY_MESSAGE,
    val transferBankTranDate: String = LocalDateTime.now().format(YMD_FORMATTER)
)
