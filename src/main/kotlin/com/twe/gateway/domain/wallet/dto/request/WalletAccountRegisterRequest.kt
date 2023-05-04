package com.tw2.prepaid.domain.wallet.dto.request

import java.time.LocalDate
import javax.validation.constraints.Min
data class WalletAccountRegisterRequest(
    val bankCodeStd: String,
    @Min(value = 7)
    val accountNum: String,
    val birthday: LocalDate,
    val clientDeviceType: String? = null,   // IO or AD
    val clientDeviceIp: String? = null,
    val clientDeviceId: String? = null,     // uuid or ssaid
    val clientDeviceNum: String? = null,    // MDN
    val clientDeviceVersion: String? = null // os version
)
