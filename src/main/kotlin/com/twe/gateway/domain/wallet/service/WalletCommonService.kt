package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.utils.toRoundInt
import com.tw2.prepaid.domain.currency.CurrencyService
import com.tw2.prepaid.domain.member.model.repository.PartnerMemberRepository
import com.tw2.prepaid.domain.member.model.repository.PartnerRepository
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.entity.WalletPocket
import com.tw2.prepaid.domain.wallet.model.repository.WalletPocketRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class WalletCommonService(
    private val walletPocketRepository: WalletPocketRepository,
    private val walletRepository: WalletRepository,
    private val partnerMemberRepository: PartnerMemberRepository,
    private val currencyService: CurrencyService,
    private val partnerRepository: PartnerRepository,
) {
    fun getWalletWithLock(walletId: Long) = walletRepository.queryById(walletId)
        ?: throw DefaultException(errorCode = ErrorCode.WALLET_NOT_FOUND)
    fun getWalletNoLocking(walletId: Long) = walletRepository.findByIdNoLocking(walletId)
        ?: throw DefaultException(errorCode = ErrorCode.WALLET_NOT_FOUND)
    fun getPartnerMember(userId: Long) = partnerMemberRepository.findByIdOrNull(userId)
        ?: throw DefaultException(errorCode = ErrorCode.USER_NOT_FOUND)
    fun getPocket(walletId: Long, currency: String) = walletPocketRepository.findByWalletIdAndCurrency(walletId, currency)
        ?: throw DefaultException(errorCode = ErrorCode.POCKET_NOT_FOUND)
    fun getOrCreatePocket(wallet: Wallet, currency: String) = walletPocketRepository.findByWalletIdAndCurrency(wallet.id, currency)
        ?: walletPocketRepository.save(WalletPocket(wallet = wallet, currency = currency))
    fun getTotalBalance(pockets: List<WalletPocket>): Int =
        pockets.sumOf { it.totalAmount() * currencyService.baseCurrencyExchangeRate(it.currency) }.toRoundInt()
    fun getPointBalance(pockets: List<WalletPocket>): Int =
        pockets.sumOf { it.pointBalance * currencyService.baseCurrencyExchangeRate(it.currency) }.toRoundInt()
    fun getCashBalance(pockets: List<WalletPocket>): Int =
        pockets.sumOf { it.cashBalance * currencyService.baseCurrencyExchangeRate(it.currency) }.toRoundInt()
    fun getPartner(partnerId: Long) = partnerRepository.findByIdOrNull(partnerId)
        ?: throw DefaultException(errorCode = ErrorCode.PARTNER_INFO_NOT_FOUND)
}