package com.tw2.prepaid.common.error

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.FAILURE_CODE
import com.tw2.prepaid.common.SUCCESS_CODE

const val PREPAID_BASE_ERROR_CODE = 1000
const val USER_BASE_ERROR_CODE = 10000
const val OB_BASE_ERROR_CODE = 5000

data class ErrorCodeHolder(
    val errorCode: ErrorCode,
    val isErrorLog: Boolean = true,
    val msg: String = EMPTY_MESSAGE,
) {
    fun getMessage() = msg.ifBlank { errorCode.message }
}
enum class ErrorCode(
    val code: Int,
    val message: String = EMPTY_MESSAGE
) {
    잔액부족(PREPAID_BASE_ERROR_CODE + 1, "잔액 또는 포인트가 부족합니다."),
    원거래없음(PREPAID_BASE_ERROR_CODE + 2, "원거래가 존재하지 않습니다."),
    충전금액초과(PREPAID_BASE_ERROR_CODE + 3, "충전 금액이 한도초과하였습니다."),
    BAD_REQUEST(PREPAID_BASE_ERROR_CODE + 4, "잘못된 형식의 요청입니다."),
    최신환율아님(PREPAID_BASE_ERROR_CODE + 5, "현재의 환율 값이 아닙니다."),
    INVALID_CURRENCY(PREPAID_BASE_ERROR_CODE + 6, "지원하지 않는 통화."),
    NOT_EXIST_MAIN_ACCOUNT(PREPAID_BASE_ERROR_CODE + 7, "메인 계좌가 존재하지 않음."),
    NOT_EXIST_TARGET_ACCOUNT(PREPAID_BASE_ERROR_CODE + 8, "계좌가 존재하지 않음."),
    WALLET_NOT_FOUND(PREPAID_BASE_ERROR_CODE + 9, "지갑이 존재하지 않음."),
    USER_NOT_FOUND(PREPAID_BASE_ERROR_CODE + 10, "유저가 존재하지 않음."),
    POCKET_NOT_FOUND(PREPAID_BASE_ERROR_CODE + 11, "포켓이 존재하지 않음."),
    포인트부족(PREPAID_BASE_ERROR_CODE + 12, "포인트가 부족합니다."),
    NOT_POSITIVE_AMOUNT(PREPAID_BASE_ERROR_CODE + 13, "금액이 양수가 아닌 값으로 들어옴."),
    USER_INFO_NOT_FOUND(PREPAID_BASE_ERROR_CODE + 14, "유저 필수 정보가 존재하지 않음"),
    PARTNER_INFO_NOT_FOUND(PREPAID_BASE_ERROR_CODE + 15, "제휴사 정보가 존재하지 않음"),
    DATA_NOT_FOUND(PREPAID_BASE_ERROR_CODE + 16, "요청 데이터가 존재하지 않습니다."),
    DUPLICATE_REQUEST(PREPAID_BASE_ERROR_CODE + 17, "중복된 요청"),
    INTERNAL_DATA_INTEGRITY(PREPAID_BASE_ERROR_CODE + 18, "내부 데이터 정합성 에러"),

    오픈뱅킹에러(OB_BASE_ERROR_CODE, "오픈뱅킹에러"),
    PROCESSING_BANK_TRANSFER(OB_BASE_ERROR_CODE + 1, "은행쪽 장애로 입출금 처리중인 상태입니다."),
    EXCHANGE_ERROR_PROCESSING(OB_BASE_ERROR_CODE + 2, "은행기관 입출금 처리중이므로 나중에 시도하세요. 은행기관 오류 확인시 추후 입금 처리됩니다."),

    SUCCESS(SUCCESS_CODE),
    FAILURE(FAILURE_CODE)
}