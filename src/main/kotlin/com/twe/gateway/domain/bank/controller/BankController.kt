package com.tw2.prepaid.domain.bank.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.bank.dto.request.BankDepositRequest
import com.tw2.prepaid.domain.bank.service.BankService
import com.tw2.prepaid.domain.bank.dto.request.BankWithdrawRequest
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.bank.service.OpenbankTokenService
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.*

const val OB_REFRESH_TOKEN_PATH = "/openbanking-access-token"
@RestController
@RequestMapping(path = ["/prepaid/v1.0/banks"])
class BankController(
    private val bankService: BankService,
    private val openbankService: OpenbankService,
    private val openbankTokenService: OpenbankTokenService,
) {
    @PostMapping
    fun createBank(@RequestBody request: com.tw2.prepaid.domain.bank.dto.request.BankRequest) =
        Response(data = bankService.createBank(request))

    @PutMapping("/{id}")
    fun updateBank(@PathVariable id: Long, @RequestBody request: com.tw2.prepaid.domain.bank.dto.request.BankRequest) =
        Response(data = bankService.updateBank(id, request))

    @GetMapping("/{id}")
    fun getBank(@PathVariable id: Long) = Response(data = bankService.getBank(id))
    @GetMapping("/warm-up")
    @Hidden
    fun getBanks() = Response(data = bankService.getBanksForWarmUp())
    @GetMapping
    fun getBanks(@RequestParam isActive: Boolean? = true) =
        Response(data = bankService.getBanks(isActive))

    @GetMapping("/{fintechUseNum}/balance")
    fun getBalance(@PathVariable fintechUseNum: String) = Response(
        data = openbankService.getAccountBalance(fintechUseNum)
    )
    @PostMapping("/{fintechUseNum}/deposit")
    fun deposit(@PathVariable fintechUseNum: String, @RequestBody request: BankDepositRequest): Response<Unit> =
        Response(data = bankService.deposit(fintechUseNum, request))
    @PostMapping("/{fintechUseNum}/withdraw")
    fun withdraw(@PathVariable fintechUseNum: String, @RequestBody request: BankWithdrawRequest): Response<Unit> =
        Response(data = bankService.withdraw(fintechUseNum, request))
    @GetMapping(OB_REFRESH_TOKEN_PATH)
    fun getObAccessToken() = Response(data = openbankTokenService.getAccessToken())
}