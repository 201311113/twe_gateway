package com.tw2.prepaid.common.support.retry

import com.tw2.prepaid.common.error.RetryAbortException
import com.tw2.prepaid.common.utils.toObject
import mu.KotlinLogging

private val log = KotlinLogging.logger {}
interface RetryExecutor {
    val retryJobType: RetryJobType
    fun execute(retryJob: RetryJob) {
        val params = toObject<Map<String, Any>>(retryJob.data)
        val preparedReq = doOnPrepare(params)
        runCatching {
            throwIfErrorResult(preparedReq)
            doOnSuccess(params)
        }.onFailure {
            if (it is RetryAbortException)
                doOnRetryAbort(params)
            throw it
        }
    }
    fun isAssignable(retryJobType: RetryJobType) = retryJobType == this.retryJobType
    fun doOnPrepare(params: Map<String, Any>): Any = Any()
    fun throwIfErrorResult(req: Any) {}
    fun doOnRetryAbort(params: Map<String, Any>) {}
    fun doOnSuccess(params: Map<String, Any>) {}
}