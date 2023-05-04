package com.tw2.prepaid.domain.bank.model

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.DefaultException

enum class BankCode(
    val code: String
) {
    KDB산업은행(code = "002"),
    IBK기업은행(code = "003"),
    KB국민은행(code = "004"),
    수협은행(code = "007"),
    NH농협은행(code = "011"),
    우리은행(code = "020"),
    SC제일은행(code = "023"),
    한국씨티은행(code = "027"),
    대구은행(code = "031"),
    부산은행(code = "032"),
    광주은행(code = "034"),
    제주은행(code = "035"),
    전북은행(code = "037"),
    경남은행(code = "039"),
    하나은행(code = "081"),
    신한은행(code = "088"),
    케이뱅크(code = "089"),
    카카오뱅크(code = "090"),
    토스뱅크(code = "092"),
    오픈은행(code = "097"),
    농협중앙회(code = "012"),
    새마을금고중앙회(code = "045"),
    신협중앙회(code = "048"),
    저축은행중앙회(code = "050"),
    산림조합중앙회(code = "064"),
    우정사업본부(code = "071"),
    KB증권(code = "218"),
    KTB투자증권(code = "227"),
    미래에셋증권(code = "238"),
    삼성증권(code = "240"),
    한국투자증권(code = "243"),
    NH투자증권(code = "247"),
    교보증권(code = "261"),
    하이투자증권(code = "262"),
    현대차증권(code = "263"),
    키움증권(code = "264"),
    이베스트투자증권(code = "265"),
    SK증권(code = "266"),
    대신증권(code = "267"),
    한화투자증권(code = "269"),
    하나증권(code = "270"),
    토스증권(code = "271"),
    신한금융투자(code = "278"),
    DB금융투자(code = "279"),
    유진투자증권(code = "280"),
    메리츠증권(code = "287"),
    오픈증권(code = "296"),
}

fun getBankName(code: String) = BankCode.values().find { it.code == code }?.name
    ?: throw DefaultException(message = "없는 뱅크 코드입니다.")
fun getBankNameDefaultEmpty(code: String?) = BankCode.values().find { it.code == code }?.name ?: EMPTY_MESSAGE
enum class BankType {
    BANK, SECURITIES
}