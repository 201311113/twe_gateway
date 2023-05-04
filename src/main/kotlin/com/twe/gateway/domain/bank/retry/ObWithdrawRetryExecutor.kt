package com.tw2.prepaid.domain.bank.retry

import com.tw2.prepaid.common.error.RetryAbortException
import com.tw2.prepaid.common.support.retry.RetryJobType
import com.tw2.prepaid.common.utils.mapper
import com.tw2.prepaid.domain.bank.common.YMDT_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferDetailsRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankWithdrawRequest
import com.tw2.prepaid.domain.bank.model.entity.OpenbankTransferHistory
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.wallet.dto.request.internal.WalletChargeInternalRequest
import com.tw2.prepaid.domain.wallet.service.WalletExchangeService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

const val WALLET_CHARGE_REQ = "walletChargeReq"
@Component
class ObWithdrawRetryExecutor (
    openbankService: OpenbankService,
    val walletExchangeService: WalletExchangeService,
): AbstractObTransferRetryExecutor(openbankService) {
    override val retryJobType: RetryJobType = RetryJobType.OB_WITHDRAW
    override fun doOnPrepare(params: Map<String, Any>): Any {
        // withdraw 시에는 history 저장을 못한 상태임
        openbankService.changeHistoryTransferStatus(mapper.convertValue(params[OB_HISTORY_REQ], OpenbankTransferHistory::class.java))
        val withdrawReq = mapper.convertValue(params[RetryJobType.OB_WITHDRAW.name], OpenbankWithdrawRequest::class.java)
        return OpenbankTransferRequest(
            checkType = OpenbankTransferCheckType.WITHDRAW,
            reqList = listOf(
                OpenbankTransferDetailsRequest(
                    orgBankTranId = withdrawReq.bankTranId,
                    orgBankTranDate = LocalDateTime.parse(withdrawReq.tranDtime, YMDT_FORMATTER).format(YMD_FORMATTER),
                    orgTranAmt = withdrawReq.tranAmt
                )
            )
        )
    }
    // TODO 오픈뱅킹의 도메인과 지갑의 도메인이 섞여있어 맘에 안들긴함. 리팩토링 필요
    override fun doOnSuccess(params: Map<String, Any>) {
        try {
            super.doOnSuccess(params)
            val chargeReq = params[WALLET_CHARGE_REQ]
            if (chargeReq != null) {
                walletExchangeService.chargeWalletDb(req = mapper.convertValue(chargeReq, WalletChargeInternalRequest::class.java))
            }
        } catch (th: Throwable) {
            throw RetryAbortException(th)
        }
    }
}