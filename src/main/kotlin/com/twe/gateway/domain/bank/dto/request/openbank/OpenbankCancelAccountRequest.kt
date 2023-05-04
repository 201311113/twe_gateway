package com.tw2.prepaid.domain.bank.dto.request.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankCancelAccountRequest(
    val bankTranId: String = makeBankTranId(),
    val scope: OpenbankAccountRegisterType,
    val fintechUseNum: String,
    val userSeqNo: String? = null,
    val bankCodeStd: String? = null,
    val accountNum: String? = null,
    val accountSeq: String? = null
)