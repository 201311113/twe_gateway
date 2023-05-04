package com.tw2.prepaid.domain.bank.simulator.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.model.OpenbankCntrAccountType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankDepositRequest(
    val cntrAccountType: Char = OpenbankCntrAccountType.ACCOUNT.code,
    val cntrAccountNum: String,
    val wdPassPhrase: String,
    val wdPrintContent: String,
    val nameCheckOption: String = "off",
    val subFrncName: String? = null,
    val subFrncBusinessNum: String? = null,
    val tranDtime: String = makeTranDtime(),
    val reqList: List<SimulatorOpenbankDepositTargetRequest>,
    val reqCnt: Int = reqList.size
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankDepositTargetRequest(
    val tranNo: String,
    val bankTranId: String = makeBankTranId(),
    val fintechUseNum: String,
    val printContent: String,
    val tranAmt: String,
    val reqClientName: String,
    val reqClientBankCode: String? = null,
    val reqClientAccountNum: String? = null,
    val reqClientFintechUseNum: String? = null,
    val reqClientNum: String,
    val transferPurpose: String = "TR",
    val recvBankTranId: String? = null,
    val cmsNum: String? = null,
    val withdrawBankTranId: String? = null
)
