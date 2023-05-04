package com.tw2.prepaid.domain.bank.retry

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.error.IgnoreException
import com.tw2.prepaid.common.error.RetryAbortException
import com.tw2.prepaid.common.support.retry.RetryExecutor
import com.tw2.prepaid.common.utils.mapper
import com.tw2.prepaid.domain.bank.model.OpenbankTransferResultEnum
import com.tw2.prepaid.domain.bank.model.entity.OpenbankTransferHistory
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankTransferRequest
import com.tw2.prepaid.domain.bank.service.OpenbankService

const val OB_HISTORY_REQ = "OB_HISTORY_REQ"
sealed class AbstractObTransferRetryExecutor(
    val openbankService: OpenbankService
): RetryExecutor {
    override fun throwIfErrorResult(req: Any) {
        try {
            openbankService.getTransferResult(req as OpenbankTransferRequest)
        } catch (ex: DefaultException) {
            when (ex.errorCode) {
                ErrorCode.PROCESSING_BANK_TRANSFER -> throw IgnoreException()
                else -> throw RetryAbortException()
            }
        }
    }
    override fun doOnSuccess(params: Map<String, Any>) {
        openbankService.changeHistoryTransferStatus(
            mapper.convertValue(params[OB_HISTORY_REQ], OpenbankTransferHistory::class.java),
            OpenbankTransferResultEnum.RETRY_DONE)
    }
    override fun doOnRetryAbort(params: Map<String, Any>) {
        openbankService.changeHistoryTransferStatus(
            mapper.convertValue(params[OB_HISTORY_REQ], OpenbankTransferHistory::class.java),
            OpenbankTransferResultEnum.RETRY_FAILED
        )
    }
}
