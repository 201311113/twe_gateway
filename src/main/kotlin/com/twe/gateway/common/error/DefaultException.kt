package com.tw2.prepaid.common.error

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.FAILURE_CODE
import org.springframework.http.HttpStatus
class DefaultException: PrepaidException {
    constructor(
        httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
        code: Int = FAILURE_CODE,
        errorCode: ErrorCode? = null,
        message: String? = null,
        data: Any? = null,
        isErrorLog: Boolean = true
    ): super(
        httpStatus = httpStatus, code = code, errorCode = errorCode,
        message = message ?: errorCode?.message ?: EMPTY_MESSAGE, data = data,
        isErrorLog = isErrorLog
    )
    constructor(errorCodeHolder: ErrorCodeHolder, data: Any? = null): this(
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        code = errorCodeHolder.errorCode.code,
        message = errorCodeHolder.getMessage(),
        isErrorLog = errorCodeHolder.isErrorLog,
        data = data,
    )
}