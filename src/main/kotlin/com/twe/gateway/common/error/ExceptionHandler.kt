package com.tw2.prepaid.common.error

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.FAILURE_CODE
import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.bank.feign.feignExceptionHandling
import feign.FeignException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

private val log = KotlinLogging.logger {}

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(value = [Exception::class])
    fun handleException(e: Exception): ResponseEntity<Response<Any?>> {
        return when(e) {
            is PrepaidException -> errorResponse(
                httpStatus = e.httpStatus, code = e.errorCode?.code ?: e.code, data = e.data,
                e = e, message = e.message ?: e.errorCode?.message ?: EMPTY_MESSAGE
            )
            is MethodArgumentTypeMismatchException,
            is HttpMessageNotReadableException,
            is MethodArgumentNotValidException -> errorResponse(
                httpStatus = HttpStatus.BAD_REQUEST, e = e, errorCode = ErrorCode.BAD_REQUEST
            )
            is FeignException -> errorResponse(
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, e = feignExceptionHandling(e), message = e.message ?: EMPTY_MESSAGE,
            )
            else -> errorResponse(e = e)
        }
    }
    private fun errorResponse(
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        code: Int = FAILURE_CODE,
        e: Exception,
        message: String = EMPTY_MESSAGE,
        data: Any? = null,
    ): ResponseEntity<Response<Any?>> {
        when (e) {
            is IgnoreException -> {}
            is PrepaidException -> {
                if (e.isErrorLog) log.error("business exception.", e)
                else log.info("ignore exception", e)
            }
            else -> {
                log.error("business exception.", e)
            }
        }
        return ResponseEntity.status(httpStatus).body(Response(code = code, message = message, data = data))
    }
    private fun errorResponse(
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        errorCode: ErrorCode,
        e: Exception,
    ): ResponseEntity<Response<Any?>> {
        when (e) {
            is IgnoreException -> {}
            is PrepaidException -> {
                if (e.isErrorLog) log.error("business exception.", e)
                else log.info("ignore exception", e)
            }
            else -> {
                log.error("business exception.", e)
            }
        }
        return ResponseEntity.status(httpStatus).body(Response(errorCode = errorCode))
    }
}