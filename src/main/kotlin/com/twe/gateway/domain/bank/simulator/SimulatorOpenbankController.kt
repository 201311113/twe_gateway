package com.tw2.prepaid.domain.bank.simulator

import com.fasterxml.jackson.databind.node.ObjectNode
import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.feign.*
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankRefreshAccessTokenRequest
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankRefreshAccessTokenResponse
import com.tw2.prepaid.domain.bank.simulator.dto.*
import io.swagger.v3.oas.annotations.Hidden
import mu.KotlinLogging
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
private val log = KotlinLogging.logger {}
@RestController
@Hidden
class SimulatorOpenbankController(
    private val simulatorOpenbankService: SimulatorOpenbankService,
    private val restTemplate: RestTemplate,
) {
    @PostMapping("/proxy-test")
    fun proxyTest(@RequestBody req: ProxyTestRequest) {
        val ss = restTemplate.exchange(req.url, HttpMethod.GET, null, ObjectNode::class.java)
    }
    @PostMapping(TRANSFER_RESULT_PATH)
    fun getTransferResult(@RequestBody req: SimulatorOpenbankTransferRequest): SimulatorOpenbankTransferResponse =
        simulatorOpenbankService.getTransferResult(req)

    @PostMapping(WITHDRAW_PATH)
    fun withdraw(@RequestBody req: SimulatorOpenbankWithdrawRequest): SimulatorOpenbankWithdrawResponse =
        simulatorOpenbankService.withdraw(req)

    @PostMapping(DEPOSIT_PATH)
    fun deposit(@RequestBody req: SimulatorOpenbankDepositRequest): SimulatorOpenbankDepositResponse =
        simulatorOpenbankService.deposit(req)

    @PostMapping(CLOSE_USER_PATH)
    fun closeUser(@RequestBody req: SimulatorOpenbankCloseUserRequest): SimulatorOpenbankCloseUserResponse =
        simulatorOpenbankService.closeUser(req)

    @PostMapping(CANCEL_ACCOUNT_PATH)
    fun cancelAccount(@RequestBody req: SimulatorOpenbankCancelAccountRequest): SimulatorOpenbankCancelAccountResponse =
        simulatorOpenbankService.cancelAccount(req)

    @PostMapping(REGISTER_ACCOUNT_PATH)
    fun registerAccount(@RequestBody req: SimulatorOpenbankRegisterAccountRequest): SimulatorOpenbankRegisterAccountResponse =
        simulatorOpenbankService.registerAccount(req)

    @GetMapping(GET_ACCOUNTS_PATH)
    fun getAccounts(
        @RequestParam(name = "user_seq_no") userSeqNo: String,
        @RequestParam(name = "include_cancel_yn") includeCancelYn: Char = 'N',
        @RequestParam(name = "sort_order") sortOrder: Char = 'D'
    ): SimulatorOpenbankAccountsResponse = simulatorOpenbankService.getAccounts(userSeqNo = userSeqNo)

    @GetMapping(GET_ACCOUNT_BALANCE_PATH)
    fun getAccountBalance(
        @RequestParam(name = "bank_tran_id") bankTranId: String = makeBankTranId(),
        @RequestParam(name = "fintech_use_num") fintechUseNum: String,
        @RequestParam(name = "tran_dtime") tranDtime: String = makeTranDtime()
    ): SimulatorOpenbankAccountBalanceResponse = simulatorOpenbankService.getAccountBalance(bankTranId = bankTranId, fintechUseNum = fintechUseNum)

    @PostMapping(REFRESH_ACCESS_TOKEN_PATH, consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun refreshAccessToken(@RequestBody req: String): OpenbankRefreshAccessTokenResponse {
        log.info(req)
        return simulatorOpenbankService.refreshAccessToken(OpenbankRefreshAccessTokenRequest())
    }
}