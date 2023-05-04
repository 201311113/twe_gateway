package com.tw2.prepaid.domain.bank.dto.response

import com.tw2.prepaid.common.YnType
import com.tw2.prepaid.domain.bank.model.OpenbankAccountStateType
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankAccountResponse

data class BankAccount(
    val fintechUseNum: String,
    val bankCodeStd: String,
    val bankName: String,
    val accountNum: String,
    val accountNumMasked: String,
    var isMain: Boolean = false,
    val isAvailable: Boolean = true,
    val isInquiryAgree: Boolean,
    val isTransferAgree: Boolean,
    val isActiveAccount: Boolean,
) {
    companion object {
        fun create(response: OpenbankAccountResponse, isAvailable: Boolean): BankAccount = response.run {
            BankAccount(
                fintechUseNum = fintechUseNum,
                bankCodeStd = bankCodeStd,
                bankName = bankName,
                accountNum = accountNum,
                accountNumMasked = accountNumMasked,
                isAvailable = isAvailable,
                isInquiryAgree = inquiryAgreeYn == YnType.Y,
                isTransferAgree = transferAgreeYn == YnType.Y,
                isActiveAccount = accountState == OpenbankAccountStateType.USE
            )
        }
    }
}
