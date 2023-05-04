package com.tw2.prepaid.domain.bank.retry

import com.tw2.prepaid.common.support.retry.RetryJobType
import com.tw2.prepaid.common.utils.mapper
import com.tw2.prepaid.domain.bank.common.YMDT_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferDetailsRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferRequest
import com.tw2.prepaid.domain.bank.service.OpenbankService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ObDepositRetryExecutor (
    openbankService: OpenbankService,
): AbstractObTransferRetryExecutor(openbankService) {
    override val retryJobType: RetryJobType = RetryJobType.OB_DEPOSIT
    override fun doOnPrepare(params: Map<String, Any>): Any {
        val depositReq = mapper.convertValue(params[RetryJobType.OB_DEPOSIT.name], OpenbankDepositRequest::class.java)
        return OpenbankTransferRequest(
            checkType = OpenbankTransferCheckType.DEPOSIT,
            reqList = listOf(
                OpenbankTransferDetailsRequest(
                    orgBankTranId = depositReq.reqList[0].bankTranId,
                    orgBankTranDate = LocalDateTime.parse(depositReq.tranDtime, YMDT_FORMATTER).format(YMD_FORMATTER),
                    orgTranAmt = depositReq.reqList[0].tranAmt
                )
            )
        )
    }
}