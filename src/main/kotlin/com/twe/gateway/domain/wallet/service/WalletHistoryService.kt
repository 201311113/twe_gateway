package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.utils.mapper
import com.tw2.prepaid.domain.currency.model.CurrencyType
import com.tw2.prepaid.domain.member.model.PartnerPaymentType
import com.tw2.prepaid.domain.wallet.dto.response.*
import com.tw2.prepaid.domain.wallet.model.WalletActionType.*
import com.tw2.prepaid.domain.wallet.model.WalletActionType
import com.tw2.prepaid.domain.wallet.model.WalletHistoryType
import com.tw2.prepaid.domain.wallet.model.entity.*
import com.tw2.prepaid.domain.wallet.model.repository.*
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

private val log = KotlinLogging.logger {}
@Service
class WalletHistoryService(
    private val walletHistoryRepository: WalletHistoryRepository,
    private val walletHistoryExchangeDetailRepository: WalletHistoryExchangeDetailRepository,
    private val walletHistoryObDetailRepository: WalletHistoryObDetailRepository,
) {
    fun getWalletHistory(pageable: Pageable, walletId: Long, currency: String?,
                         startDt: LocalDate?, endDt: LocalDate?, actionTypes: List<WalletActionType>?): Page<WalletHistoryResponse> {
        if (startDt != null && endDt != null && startDt.isAfter(endDt))
            throw DefaultException(errorCode = ErrorCode.BAD_REQUEST)

        return walletHistoryRepository.findWalletHistories(
            pageable = pageable, walletId = walletId,
            currency = currency, startDt = startDt,
            endDt = endDt, actionTypes = actionTypes
        ).map(::createFromEntity)
    }
    fun getWalletHistoryDetail(walletId: Long, historyId: Long, historyDt: LocalDate?): WalletHistoryDetailResponse =
        // DB partitioning 고려
        walletHistoryRepository.findWalletHistory(historyId, historyDt)?.let {
            if (it.walletId != walletId) throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND)
            when (it.actionType.isExchangeDetail) {
                true -> {
                    val detailHis = walletHistoryExchangeDetailRepository.findByWalletHistoryId(it.id, historyDt)
                        ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND)
                    WalletHistoryDetailResponse.createFromEntity(it, detailHis)
                }
                false -> WalletHistoryDetailResponse.createFromEntity(it)
            }
        } ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND)
    fun saveWalletCardHistory(
        wallet: Wallet,
        pocket: WalletPocket,
        actionType: WalletActionType,
        cashAmount: BigDecimal = BigDecimal.ZERO,
        pointAmount: BigDecimal = BigDecimal.ZERO,
        accountTxId: String,
        additionalInfos: Map<String, Any?> = emptyMap()
    ): Long {
        val historyId = walletHistoryRepository.save(
            WalletHistory(
                userId = wallet.user.id,
                walletId = wallet.id,
                currency = pocket.currency,
                actionType = actionType,
                cashAmount = cashAmount,
                pointAmount = pointAmount,
                cashBalance = pocket.cashBalance,
                pointBalance = pocket.pointBalance,
                additionalInfos = mapper.writeValueAsString(additionalInfos),
            )
        ).id
        walletHistoryObDetailRepository.save(
            WalletHistoryObDetail(
                walletHistoryId = historyId,
                walletId = wallet.id,
                accountTxId = accountTxId,
                actionType = actionType.name,
            )
        )
        return historyId
    }
    fun saveWalletExchangeHistory(
        wallet: Wallet,
        pocket: WalletPocket,
        actionType: WalletActionType,
        historyType: WalletHistoryType,
        localCashAmount: BigDecimal,
        localPointAmount: BigDecimal = BigDecimal.ZERO,
        krwCashAmount: Int = 0,
        krwPointAmount: Int = 0,
        baseExchangeRate: BigDecimal,
        spreadExchangeRate: BigDecimal,
        usdSpreadExchangeRate: BigDecimal,
        twFee: BigDecimal = BigDecimal.ZERO,
        krwExchangeGains: BigDecimal = BigDecimal.ZERO,
        businessUuid: String,
        partnerPaymentType: PartnerPaymentType,
        additionalInfos: Map<String, Any> = emptyMap(),
        additionalInfoDetails: Map<String, Any> = emptyMap(),
    ) {
        val walletHistory = walletHistoryRepository.save(
            WalletHistory(
                userId = wallet.user.id,
                walletId = wallet.id,
                currency = pocket.currency,
                actionType = actionType,
                cashAmount = localCashAmount,
                pointAmount = localPointAmount,
                cashBalance = pocket.cashBalance,
                pointBalance = pocket.pointBalance,
                additionalInfos = mapper.writeValueAsString(additionalInfos),
            )
        )
        walletHistoryExchangeDetailRepository.save(
            WalletHistoryExchangeDetail(
                walletHistoryId = walletHistory.id,
                walletId = wallet.id,
                historyType = historyType,
                baseExchangeRate = baseExchangeRate.toDouble(),
                spreadExchangeRate = spreadExchangeRate.toDouble(),
                usdSpreadExchangeRate = usdSpreadExchangeRate.toDouble(),
                feeAmount = twFee,
                krwExchangeGains = krwExchangeGains.toDouble(),
                businessUuid = businessUuid,
                partnerPaymentType = partnerPaymentType,
                krwCashAmount = krwCashAmount,
                krwPointAmount = krwPointAmount,
                additionalInfos = mapper.writeValueAsString(additionalInfoDetails),
            )
        )
    }
}