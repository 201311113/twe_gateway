package com.tw2.prepaid.domain.bank.dto.request

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositTargetRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.member.model.entity.PartnerMember

data class BankDepositRequest(
    val twPrintContent: String,
    val userPrintContent: String,
    val tranAmt: Int,
    val walletId: Long,
) {
    fun toObDepositRequest(
        fintechUseNum: String, user: PartnerMember, passPhrase: String) = OpenbankDepositRequest(
            wdPrintContent = twPrintContent,
            wdPassPhrase = passPhrase,
            reqList = listOf(OpenbankDepositTargetRequest(
                tranNo = "1",
                fintechUseNum = fintechUseNum,
                reqClientFintechUseNum = fintechUseNum,
                printContent = userPrintContent,
                tranAmt = tranAmt.toString(),
                reqClientName = user.member.name ?: throw DefaultException(errorCode = ErrorCode.USER_INFO_NOT_FOUND),
                reqClientNum = user.userSeqNum ?: throw DefaultException(errorCode = ErrorCode.USER_INFO_NOT_FOUND)
            ))
        )
}