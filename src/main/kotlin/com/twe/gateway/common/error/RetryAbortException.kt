package com.tw2.prepaid.common.error

import java.lang.RuntimeException

class RetryAbortException: RuntimeException {
    constructor(): super()
    constructor(throwable: Throwable): super(throwable)
}