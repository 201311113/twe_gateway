package com.tw2.prepaid.common.error

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.FAILURE_CODE
import org.springframework.http.HttpStatus
import java.lang.RuntimeException

open class PrepaidException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val code: Int = FAILURE_CODE,
    val errorCode: ErrorCode? = null,
    val data: Any? = null,
    val isErrorLog: Boolean = true,
    message: String = EMPTY_MESSAGE
): RuntimeException(message)