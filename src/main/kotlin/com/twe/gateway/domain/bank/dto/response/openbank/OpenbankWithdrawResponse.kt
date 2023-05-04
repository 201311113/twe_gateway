package com.tw2.prepaid.domain.bank.dto.response.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.MILLIS_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType
import com.tw2.prepaid.domain.bank.model.OpenbankTransferResultEnum
import com.tw2.prepaid.domain.bank.model.entity.OpenbankTransferHistory
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankWithdrawResponse(
    val apiTranId: String = UUID.randomUUID().toString(),
    val apiTranDtm: String = LocalDateTime.now().format(MILLIS_FORMATTER),
    val rspCode: String,
    val rspMessage: String = EMPTY_MESSAGE,
    val dpsBankCodeStd: String = EMPTY_MESSAGE,
    val dpsBankCodeSub: String = EMPTY_MESSAGE,
    val dpsBankName: String = EMPTY_MESSAGE,
    val dpsAccountNumMasked: String = EMPTY_MESSAGE,
    val dpsPrintContent: String = EMPTY_MESSAGE,
    val dpsAccountHolderName: String = EMPTY_MESSAGE,
    val bankTranId: String = EMPTY_MESSAGE,
    val bankTranDate: String = LocalDateTime.now().format(YMD_FORMATTER),
    val bankCodeTran: String = EMPTY_MESSAGE,
    val bankRspCode: String = EMPTY_MESSAGE,
    val bankRspMessage: String = EMPTY_MESSAGE,
    val fintechUseNum: String = EMPTY_MESSAGE,
    val accountAlias: String = EMPTY_MESSAGE,
    val bankCodeStd: String = EMPTY_MESSAGE,
    val bankCodeSub: String = EMPTY_MESSAGE,
    val bankName: String = EMPTY_MESSAGE,
    val savingsBankName: String = EMPTY_MESSAGE,
    val accountNumMasked: String = EMPTY_MESSAGE,
    val printContent: String = EMPTY_MESSAGE,
    val accountHolderName: String = EMPTY_MESSAGE,
    val tranAmt: String = EMPTY_MESSAGE,
    val wdLimitRemainAmt: String = EMPTY_MESSAGE
) {
    fun toOpenbankTransferHistoryEntity(userSeqNum: String,
                                        userId: Long,
                                        walletId: Long,
                                        businessUuid: String,
                                        tranResult: OpenbankTransferResultEnum) =
        OpenbankTransferHistory(
            userId = userId,
            walletId = walletId,
            fintechNum = fintechUseNum,
            userSeqNum = userSeqNum,
            transferType = OpenbankTransferCheckType.WITHDRAW,
            tranAmt = tranAmt.toInt(),
            tranNo = 1,
            bankTranId = bankTranId,
            apiTranId = apiTranId,
            apiTranDtm = LocalDateTime.parse(apiTranDtm, MILLIS_FORMATTER),
            tranResult = tranResult,
            userPrintContent = printContent,
            userAccountNumMasked = accountNumMasked,
            userBankCode = bankCodeStd,
            userAccountHolderName = accountHolderName,
            partnerPrintContent = dpsPrintContent,
            partnerAccountNumMasked = dpsAccountNumMasked,
            partnerBankCode = dpsBankCodeStd,
            partnerAccountHolderName = dpsAccountHolderName,
            rspCode = rspCode,
            bankRspCode = bankRspCode,
            businessUuid = businessUuid,
        )
}
