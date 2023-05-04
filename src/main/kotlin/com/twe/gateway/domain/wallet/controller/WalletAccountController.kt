package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.bank.dto.response.BankAccount
import com.tw2.prepaid.domain.wallet.dto.request.WalletAccountRegisterRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletAccountRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPennyAuthRequest
import com.tw2.prepaid.domain.wallet.dto.response.WalletAccountResponse
import com.tw2.prepaid.domain.wallet.service.WalletAccountService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets/{walletId}"])
class WalletAccountController(
    private val walletAccountService: WalletAccountService
) {
    @PostMapping("/penny-auth")
    fun pennyAuthentication(
        @PathVariable walletId: Long,
        @RequestBody request: WalletPennyAuthRequest,
    ): Response<Unit> = Response(data = walletAccountService.pennyAuthentication(walletId, request))
    @PostMapping("/penny-callback")
    fun pennySuccessCallback(
        @PathVariable walletId: Long,
        @RequestBody request: WalletAccountRequest,
    ): Response<Unit> = Response(data = walletAccountService.pennySuccessCallback(walletId, request))
    @PostMapping("/accounts")
    fun registerAccount(
        @PathVariable walletId: Long,
        @RequestBody request: WalletAccountRegisterRequest
    ): Response<WalletAccountResponse> = walletAccountService.registerAccount(walletId, request)
    @DeleteMapping("/accounts/{fintechUseNum}")
    fun unregisterAccount(
        @PathVariable walletId: Long,
        @PathVariable fintechUseNum: String
    ): Response<Unit> = Response(data = walletAccountService.unregisterAccount(walletId, fintechUseNum))
    @PutMapping("/main-account")
    fun changeMainAccount(
        @PathVariable walletId: Long,
        @RequestBody request: WalletAccountRequest
    ): Response<Unit> = Response(
        data = walletAccountService.changeMainAccount(walletId, request)
    )
    @GetMapping("/accounts")
    fun getAccounts(
        @PathVariable walletId: Long
    ): Response<List<BankAccount>> = Response(data = walletAccountService.getCurrentAccounts(walletId))
    @GetMapping("/accounts/{fintechUseNum}")
    fun getAccount(
        @PathVariable walletId: Long,
        @PathVariable fintechUseNum: String,
    ): Response<BankAccount> = Response(data = walletAccountService.getCurrentAccount(walletId, fintechUseNum))
}