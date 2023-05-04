package com.tw2.prepaid.domain.bank.dto.request.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankDepositResponse
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankDepositTargetResponse
import com.tw2.prepaid.domain.bank.model.OpenBankApiResponseCode
import com.tw2.prepaid.domain.bank.model.OpenbankCntrAccountType
import com.tw2.prepaid.domain.bank.model.ParticipatingBankResponseCode
import javax.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankDepositRequest(
    val cntrAccountType: Char = OpenbankCntrAccountType.ACCOUNT.code,
    val cntrAccountNum: String = EMPTY_MESSAGE,
    val wdPassPhrase: String,
    @field: Size(max = 10)
    var wdPrintContent: String,
    val nameCheckOption: String = "off",
    val subFrncName: String? = null,
    val subFrncBusinessNum: String? = null,
    val tranDtime: String = makeTranDtime(),
    val reqList: List<OpenbankDepositTargetRequest>,
    val reqCnt: String = reqList.size.toString()
) {
    init {
        wdPrintContent = wdPrintContent.take(10)
    }
}
fun toObDepositResponse(req: OpenbankDepositRequest) = with(req) {
    OpenbankDepositResponse(
        rspCode = OpenBankApiResponseCode.A0000.name, wdPrintContent = wdPrintContent, resList = reqList.map(::toObDepositTargetResponse)
    )
}
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankDepositTargetRequest(
    val tranNo: String,
    val bankTranId: String = makeBankTranId(),
    val fintechUseNum: String,
    @field: Size(max = 7)
    var printContent: String,
    val tranAmt: String,
    val reqClientName: String,
    val reqClientBankCode: String? = null,
    val reqClientAccountNum: String? = null,
    val reqClientFintechUseNum: String,
    val reqClientNum: String,
    val transferPurpose: String = "TR",
    val recvBankTranId: String? = null,
    val cmsNum: String? = null,
    val withdrawBankTranId: String? = null
) {
    init {
        printContent = printContent.take(7)
    }
}
fun toObDepositTargetResponse(req: OpenbankDepositTargetRequest) = with(req) {
    OpenbankDepositTargetResponse(
        tranNo = tranNo, bankTranId = bankTranId, fintechUseNum = fintechUseNum, printContent = printContent,
        tranAmt = tranAmt, accountHolderName = reqClientName, bankRspCode = ParticipatingBankResponseCode.`000`.name
    )
}