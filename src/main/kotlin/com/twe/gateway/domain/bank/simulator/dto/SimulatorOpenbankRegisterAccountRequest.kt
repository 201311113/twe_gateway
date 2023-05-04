package com.tw2.prepaid.domain.bank.simulator.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SimulatorOpenbankRegisterAccountRequest(
    val bankTranId: String = makeBankTranId(),
    val bankCodeStd: String,
    val registerAccountNum: String,
    val registerAccountSeq: String? = null,
    val userInfo: String, // 생년월일 8자리
    val userName: String,
    val userCi: String,
    val userEmail: String? = null,
    val scope: OpenbankAccountRegisterType,
    val infoPrvdAgmtYn: Char? = null,
    val wdAgmtYn: Char? = null,
    val agmtDataType: Char? = null,
    val clientDeviceType: String? = null,
    val clientDeviceIp: String? = null,
    val clientDeviceMac: String? = null,
    val clientDeviceNum: String? = null,
    val clientDeviceVersion: String? = null
)
