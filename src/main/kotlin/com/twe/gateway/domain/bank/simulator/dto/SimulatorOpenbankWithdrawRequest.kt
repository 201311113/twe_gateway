package com.tw2.prepaid.domain.bank.simulator.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.model.OpenbankCntrAccountType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankWithdrawRequest(
    val bankTranId: String = makeBankTranId(),
    val cntrAccountType: Char = OpenbankCntrAccountType.ACCOUNT.code,
    val cntrAccountNum: String,
    val dpsPrintContent: String = "",
    val fintechUseNum: String,
    val wdPrintContent: String = "",
    val tranAmt: String,
    val tranDtime: String = makeTranDtime(),
    val reqClientName: String,
    val reqClientBankCode: String? = null,
    val reqClientAccountNum: String? = null,
    val reqClientFintechUseNum: String? = null,
    val reqClientNum: String,
    val transferPurpose: String = "TR",
    val subFrncName: String? = null,
    val subFrncNum: String? = null,
    val subFrncBusinessNum: String? = null,
    val recvClientName: String? = null,
    val recvClientBankCode: String? = null,
    val recvClientAccountNum: String? = null
)
