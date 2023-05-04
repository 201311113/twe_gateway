package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.utils.needNot
import com.tw2.prepaid.domain.member.model.entity.PartnerMember
import com.tw2.prepaid.domain.wallet.dto.request.WalletCreateRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPocketCreateRequest
import com.tw2.prepaid.domain.wallet.dto.response.*
import com.tw2.prepaid.domain.wallet.model.PointTransactionType
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import com.tw2.prepaid.domain.wallet.model.repository.WalletPocketRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletPointHistoryRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class WalletSimpleService(
    private val wcs: WalletCommonService,
    private val walletRepository: WalletRepository,
    private val walletPocketRepository: WalletPocketRepository,
    private val walletPointHistoryRepository: WalletPointHistoryRepository,
) {
    fun getWallets(partnerId: Long?, pageable: Pageable): Page<WalletResponse> =
        walletRepository.findAllByPartner(partnerId, pageable).map {
            WalletResponse.createFromEntity(entity = it, totalBalance = wcs.getTotalBalance(it.pockets))
        }
    fun getWallet(user: PartnerMember) = walletRepository.findByUser(user)
    fun getWallet(walletId: Long): WalletDetailResponse {
        val wallet = wcs.getWalletNoLocking(walletId)
        return WalletDetailResponse.createFromEntity(
            entity = wallet,
            totalBalance = wcs.getTotalBalance(wallet.pockets),
            partnerName = wallet.user.partner.name
        )
    }
    @Transactional
    fun createWallet(request: WalletCreateRequest): WalletResponse {
        val user = wcs.getPartnerMember(request.userId)
        val walletEntity = walletRepository.save(Wallet(user = user))
        return WalletResponse.createFromEntity(entity = walletEntity, totalBalance = 0)
    }
    fun getPockets(walletId: Long): List<WalletPocketResponse> =
        walletPocketRepository.findByWalletId(walletId).map(::createFromEntity)
    fun getPocketCurrencies(walletId: Long): List<String> =
        walletPocketRepository.findByWalletId(walletId).map(WalletPocket::currency)
    fun getPocket(walletId: Long, currency: String): WalletPocketResponse =
        createFromEntity(wcs.getPocket(walletId = walletId, currency = currency))
    @Transactional
    fun createPocket(walletId: Long, request: WalletPocketCreateRequest): WalletPocketResponse {
        val wallet = wcs.getWalletWithLock(walletId)
        errorIfCurrencyExists(wallet.pockets, request.currency)

        val pocket = WalletPocket(
            wallet = wallet, currency = request.currency,
            cashBalance = request.cashBalance, pointBalance = request.pointBalance
        )
        return createFromEntity(walletPocketRepository.save(pocket))
    }
    private fun errorIfCurrencyExists(pockets: List<WalletPocket>, currency: String) =
        needNot(pockets.any { it.currency == currency }, message = "already exist wallet pocket [$currency]")

    // 각각의 통화들을 원화로 환율 금액 환산 후 소수점들은 더하지만, 최종 원화금액 은 소수점 버리고 나감
    fun getTotalBalance(walletId: Long): WalletTotalBalanceResponse {
        val wallet = walletPocketRepository.findByWalletId(walletId)
        val todayPointRefundCnt = walletPointHistoryRepository.countByWalletIdAndCreatedAtBetweenAndTransactionType(
            walletId = walletId,
            startDt = LocalDate.now().atStartOfDay(),
            endDt = LocalDate.now().plusDays(1).atStartOfDay(),
            tranType = PointTransactionType.REFUND,
        )
        return WalletTotalBalanceResponse(
            krwTotalBalance = wcs.getTotalBalance(wallet),
            krwCashBalance = wcs.getCashBalance(wallet),
            todayPointRefundCnt = todayPointRefundCnt
        )
    }
}