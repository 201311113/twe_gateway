package com.tw2.prepaid.domain.bank.dto.request.openbank

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.common.YnType
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType.*
import com.tw2.prepaid.domain.bank.model.OpenbankAgreementType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenbankRegisterAccountRequest(
    val bankTranId: String = makeBankTranId(),
    val bankCodeStd: String,
    val registerAccountNum: String,
    val registerAccountSeq: String? = null,
    val userInfo: String, // 생년월일 8자리
    val userName: String,
    val userCi: String,
    val userEmail: String? = null,
    val scope: OpenbankAccountRegisterType,
    val infoPrvdAgmtYn: YnType? = if (scope == inquiry) YnType.Y else null,
    val wdAgmtYn: YnType? = if (scope == transfer) YnType.Y else null,
    val agmtDataType: Char? = if (scope == transfer) OpenbankAgreementType.ARS.code else null,
    val clientDeviceType: String? = null,   // IO or AD
    val clientDeviceIp: String? = null,
    val clientDeviceId: String? = null,     // uuid or ssaid
    val clientDeviceNum: String? = null,    // MDN
    val clientDeviceVersion: String? = null // os version
)