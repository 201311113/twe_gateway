package com.tw2.prepaid.common.response

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.SUCCESS_CODE
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.error.ErrorCodeHolder

data class Response<T>(
    val code: Int = SUCCESS_CODE,
    val message: String = EMPTY_MESSAGE,
    val data: T? = null,
) {
    constructor(errorCodeHolder: ErrorCodeHolder, data: T? = null):
        this(code = errorCodeHolder.errorCode.code, message = errorCodeHolder.getMessage(), data = data)
    constructor(errorCode: ErrorCode, message: String = errorCode.message, data: T? = null):
            this(code = errorCode.code, message = message, data = data)
}
