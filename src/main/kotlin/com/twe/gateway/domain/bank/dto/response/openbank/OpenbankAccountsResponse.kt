package com.tw2.prepaid.domain.bank.dto.response.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.YnType
import com.tw2.prepaid.domain.bank.model.OpenbankAccountStateType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankAccountsResponse(
    val apiTranId: String,
    val apiTranDtm: String,
    val rspCode: String,
    val rspMessage: String = EMPTY_MESSAGE,
    val userName: String= EMPTY_MESSAGE,
    val resCnt: String?,
    val resList: List<OpenbankAccountResponse>?
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankAccountResponse(
    val fintechUseNum: String,
    val accountAlias: String,
    val bankCodeStd: String,
    val bankCodeSub: String,
    val bankName: String,
    val savingsBankName: String,
    val accountNum: String,
    val accountNumMasked: String,
    val accountSeq: String,                      // 회차번호
    val accountHolderName: String,               // 계좌예금주명
    val accountHolderType: Char,                 // 계좌구분(P:개인)
    val accountType: Char,                       // 계좌종류 ‘1’:수시입출금, ‘2’:예적금, ‘6’:수익증권, ‘T’:종합계좌
    val inquiryAgreeYn: YnType,         // 조회서비스 동의여부
    val inquiryAgreeDtime: String,      // 조회서비스 동의일시
    val transferAgreeYn: YnType,        // 출금서비스 동의여부
    val transferAgreeDtime: String,     // 출금서비스 동의일시
    val accountState: OpenbankAccountStateType            // 계좌 상태 '01': 사용, '09': 해지
)
