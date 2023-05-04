package com.tw2.prepaid.domain.bank.dto.request.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankWithdrawResponse
import com.tw2.prepaid.domain.bank.model.OpenBankApiResponseCode
import com.tw2.prepaid.domain.bank.model.OpenbankCntrAccountType
import javax.validation.constraints.Size

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankWithdrawRequest(
    val bankTranId: String = makeBankTranId(),
    val cntrAccountType: Char = OpenbankCntrAccountType.ACCOUNT.code,
    val cntrAccountNum: String = EMPTY_MESSAGE,
    @field: Size(max = 10)
    var dpsPrintContent: String,
    val fintechUseNum: String,
    @field: Size(max = 7)
    var wdPrintContent: String,
    val tranAmt: String,
    val tranDtime: String = makeTranDtime(),
    val reqClientName: String,
    val reqClientBankCode: String? = null,
    val reqClientAccountNum: String? = null,
    val reqClientFintechUseNum: String,
    val reqClientNum: String,
    val transferPurpose: String = "TR",
    val subFrncName: String? = null,
    val subFrncNum: String? = null,
    val subFrncBusinessNum: String? = null,
    val recvClientName: String? = null,
    val recvClientBankCode: String? = null,
    val recvClientAccountNum: String? = null
) {
    init {
        wdPrintContent = wdPrintContent.take(7)
        dpsPrintContent = dpsPrintContent.take(10)
    }
}
fun toObWithdrawResponse(req: OpenbankWithdrawRequest) = with(req) {
    OpenbankWithdrawResponse(
        rspCode = OpenBankApiResponseCode.A0000.name, bankTranId = bankTranId, fintechUseNum = fintechUseNum, tranAmt = tranAmt,
    )
}