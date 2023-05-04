package com.tw2.prepaid.domain.bank.service

import com.tw2.prepaid.common.configuration.BANK_CACHE_KEY
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.support.annotation.PrimaryDb
import com.tw2.prepaid.domain.bank.dto.request.BankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.BankRequest
import com.tw2.prepaid.domain.bank.dto.response.BankResponse
import com.tw2.prepaid.domain.bank.model.repository.BankRepository
import com.tw2.prepaid.domain.bank.dto.request.BankWithdrawRequest
import com.tw2.prepaid.domain.bank.dto.response.createFromEntity
import com.tw2.prepaid.domain.wallet.service.WalletCommonService
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class BankService(
    private val bankRepository: BankRepository,
    private val openbankService: OpenbankService,
    private val wcs: WalletCommonService,
    private val pp: PrepaidProperties,
) {
    @Transactional
    fun updateBank(id: Long, request: BankRequest): BankResponse {
        val bankEntity = bankRepository.findByIdOrNull(id) ?: throw DefaultException()
        return createFromEntity(request.updateEntity(bankEntity))
    }
    @Transactional
    fun createBank(request: BankRequest) = createFromEntity(bankRepository.save(request.toEntity()))
    fun getBank(id: Long) = createFromEntity(bankRepository.findByIdOrNull(id)
        ?: throw DefaultException(message = "bank is not found."))
    fun getBanks(pageable: Pageable) = bankRepository.findAll(pageable).map(::createFromEntity)
    @Cacheable(cacheNames = [BANK_CACHE_KEY])
    fun getBanks(isActive: Boolean?) = (isActive?.let {
        bankRepository.findByIsActive(isActive)
    }?: bankRepository.findAll()).map(::createFromEntity)
    fun getBanksForWarmUp() = bankRepository.findAll().map(::createFromEntity)
    @PrimaryDb
    fun deposit(fintechUseNum: String, request: BankDepositRequest) {
        val wallet = wcs.getWalletNoLocking(request.walletId)
        val obReq = request.toObDepositRequest(
            fintechUseNum = fintechUseNum,
            passPhrase = pp.getSecretValue(SecretKey.OB_PASS_PHRASE),
            user = wallet.user
        )
        openbankService.deposit(originReq = obReq, userId = wallet.user.id, walletId = wallet.id, businessUuid = UUID.randomUUID().toString())
    }
    @PrimaryDb
    fun withdraw(fintechUseNum: String, request: BankWithdrawRequest) {
        val wallet = wcs.getWalletNoLocking(request.walletId)
        val obReq = request.toObWithdrawRequest(
            fintechUseNum = fintechUseNum,
            user = wallet.user
        )
        openbankService.withdraw(originReq = obReq, userId = wallet.user.id, walletId = wallet.id, businessUuid = UUID.randomUUID().toString())
    }
}