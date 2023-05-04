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
data class OpenbankDepositResponse(
    val apiTranId: String = UUID.randomUUID().toString(),
    val apiTranDtm: String = LocalDateTime.now().format(MILLIS_FORMATTER),
    val rspCode: String,
    val rspMessage: String = EMPTY_MESSAGE,
    val wdBankCodeStd: String = EMPTY_MESSAGE,
    val wdBankCodeSub: String = EMPTY_MESSAGE,
    val wdBankName: String = EMPTY_MESSAGE,
    val wdAccountNumMasked: String = EMPTY_MESSAGE,
    val wdPrintContent: String = EMPTY_MESSAGE,
    val wdAccountHolderName: String = EMPTY_MESSAGE,
    val resList: List<OpenbankDepositTargetResponse> = emptyList(),
    val resCnt: String = resList.size.toString(),
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankDepositTargetResponse(
    val tranNo: String = EMPTY_MESSAGE,
    val bankTranId: String = EMPTY_MESSAGE,
    val bankTranDate: String = LocalDateTime.now().format(YMD_FORMATTER),
    val bankCodeTran: String = EMPTY_MESSAGE,
    val bankRspCode: String,
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
    val cmsNum: String = EMPTY_MESSAGE, // 잘 모름
    val withdrawBankTranId: String = EMPTY_MESSAGE // 잘 모름
) {
    fun toOpenbankTransferHistoryEntity(userSeqNum: String,
                                        response: OpenbankDepositResponse,
                                        userId: Long,
                                        walletId: Long,
                                        businessUuid: String,
                                        tranResult: OpenbankTransferResultEnum) =
        OpenbankTransferHistory(
            fintechNum = fintechUseNum,
            userSeqNum = userSeqNum,
            transferType = OpenbankTransferCheckType.DEPOSIT,
            tranAmt = tranAmt.toInt(),
            tranNo = tranNo.toByte(),
            bankTranId = bankTranId,
            apiTranId = response.apiTranId,
            apiTranDtm = LocalDateTime.parse(response.apiTranDtm, MILLIS_FORMATTER),
            userId = userId,
            walletId = walletId,
            tranResult = tranResult,
            userPrintContent = printContent,
            userAccountNumMasked = accountNumMasked,
            userBankCode = bankCodeStd,
            userAccountHolderName = accountHolderName,
            partnerPrintContent = response.wdPrintContent,
            partnerAccountNumMasked = response.wdAccountNumMasked,
            partnerBankCode = response.wdBankCodeStd,
            partnerAccountHolderName = response.wdAccountHolderName,
            rspCode = response.rspCode,
            bankRspCode = bankRspCode,
            businessUuid = businessUuid,
        )
}