package com.tw2.prepaid.domain.bank.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.bank.dto.request.BankTransferResultRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankRegisterAccountRequest
import com.tw2.prepaid.domain.bank.dto.request.toObTransferRequest
import com.tw2.prepaid.domain.bank.service.OpenbankService
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/ob"])
@Hidden
class OpenbankController(
    private val openbankService: OpenbankService
) {
    @PostMapping("/transfer/result")
    @Hidden
    fun getTransferResult(@RequestBody req: BankTransferResultRequest) = Response(
        data = openbankService.getTransferResult(toObTransferRequest(req))
    )
    @PostMapping("/user/close/{userSeqNum}")
    @Hidden
    fun closeUser(@PathVariable userSeqNum: String) = Response(
        data = openbankService.closeUser(userSeqNum)
    )
    @PostMapping("/account/cancel/{fintechUseNum}")
    @Hidden
    fun cancelAccount(@PathVariable fintechUseNum: String) = Response(
        data = openbankService.unregisterAccount(fintechUseNum)
    )
    @PostMapping("/user/register")
    @Hidden
    fun registerAccount(@RequestBody req: OpenbankRegisterAccountRequest) = Response(
        data = openbankService.registerAccount(req)
    )
    @PostMapping("/accounts/{userSeqNum}")
    @Hidden
    fun getAccounts(@PathVariable userSeqNum: String) = Response(
        data = openbankService.getAccounts(userSeqNum = userSeqNum, walletId = -1)
    )
    @PostMapping("/account/balance/{fintechUseNum}")
    @Hidden
    fun getAccountBalance(@PathVariable fintechUseNum: String) = Response(
        data = openbankService.getAccountBalance(fintechUseNum)
    )
}