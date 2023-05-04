package com.tw2.prepaid.domain.bank.dto.request

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankWithdrawRequest
import com.tw2.prepaid.domain.member.model.entity.PartnerMember

data class BankWithdrawRequest(
    val twPrintContent: String,
    val userPrintContent: String,
    val tranAmt: Int,
    val walletId: Long,
) {
    fun toObWithdrawRequest(fintechUseNum: String, user: PartnerMember) =
        OpenbankWithdrawRequest(
            dpsPrintContent = twPrintContent,
            fintechUseNum = fintechUseNum,
            reqClientFintechUseNum = fintechUseNum,
            wdPrintContent = userPrintContent,
            tranAmt = tranAmt.toString(),
            reqClientName = user.member.name ?: throw DefaultException(errorCode = ErrorCode.USER_INFO_NOT_FOUND),
            reqClientNum = user.userSeqNum ?: throw DefaultException(errorCode = ErrorCode.USER_INFO_NOT_FOUND),
        )
}
