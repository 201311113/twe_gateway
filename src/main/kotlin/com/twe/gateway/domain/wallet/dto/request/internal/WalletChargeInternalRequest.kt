package com.tw2.prepaid.domain.wallet.dto.request.internal

import com.tw2.prepaid.domain.member.model.PartnerPaymentType
import com.tw2.prepaid.domain.wallet.dto.request.WalletChargeRequest
import java.math.BigDecimal

data class WalletChargeInternalRequest(
    val localCashAmount: BigDecimal,
    val localPointAmount: BigDecimal,
    val krwCashAmount: Int,
    val originalRequest: WalletChargeRequest,
    val additionalInfos: Map<String, Any>,
    val walletId: Long,
    val currency: String,
    val businessUuid: String,
    val feeAmount: BigDecimal,
    val exchangeGains: BigDecimal,
    val partnerPaymentType: PartnerPaymentType,
)
