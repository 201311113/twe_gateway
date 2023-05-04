package com.tw2.prepaid.domain.bank.feign

import com.tw2.prepaid.domain.bank.common.makeBankTranId
import com.tw2.prepaid.domain.bank.common.makeTranDtime
import com.tw2.prepaid.domain.bank.dto.request.openbank.*
import com.tw2.prepaid.domain.bank.dto.response.openbank.*
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

// https://developers.kftc.or.kr/dev/doc/open-banking

const val TRANSFER_RESULT_PATH = "/v2.0/transfer/result"
const val CLOSE_USER_PATH = "/v2.0/user/close"
const val CANCEL_ACCOUNT_PATH = "/v2.0/account/cancel"
const val REGISTER_ACCOUNT_PATH = "/v2.0/user/register"
const val GET_ACCOUNTS_PATH = "/v2.0/account/list"
const val GET_ACCOUNT_BALANCE_PATH = "/v2.0/account/balance/fin_num"
const val REFRESH_ACCESS_TOKEN_PATH = "/oauth/2.0/token"

@FeignClient(
    url = "\${external.openbank-api.url}",
    value = "ob-non-exchange-api",
    configuration = [OpenbankApiConfiguration::class]
)
interface ObNonExchangeApiClient {
    @PostMapping(TRANSFER_RESULT_PATH)
    fun getTransferResult(@RequestBody req: OpenbankTransferRequest): OpenbankTransferResponse
    @PostMapping(CLOSE_USER_PATH)
    fun closeUser(@RequestBody req: OpenbankCloseUserRequest): OpenbankCloseUserResponse
    @PostMapping(CANCEL_ACCOUNT_PATH)
    fun cancelAccount(@RequestBody req: OpenbankCancelAccountRequest): OpenbankCancelAccountResponse
    @PostMapping(REGISTER_ACCOUNT_PATH)
    fun registerAccount(@RequestBody req: OpenbankRegisterAccountRequest): OpenbankRegisterAccountResponse
    @PostMapping(REFRESH_ACCESS_TOKEN_PATH, consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    // method suffix 가 UnLogging 이면 IO 로그를 남기지 않는다. (request, response body) (중요 정보 때문에)
    fun refreshAccessTokenUnLogging(@RequestBody req: OpenbankRefreshAccessTokenRequest): OpenbankRefreshAccessTokenResponse
    @GetMapping(GET_ACCOUNTS_PATH)
    fun getAccounts(
        @RequestParam(name = "user_seq_no") userSeqNo: String,
        @RequestParam(name = "include_cancel_yn") includeCancelYn: Char = 'N',
        @RequestParam(name = "sort_order") sortOrder: Char = 'D'
    ): OpenbankAccountsResponse
    @GetMapping(GET_ACCOUNT_BALANCE_PATH)
    fun getAccountBalance(
        @RequestParam(name = "bank_tran_id") bankTranId: String = makeBankTranId(),
        @RequestParam(name = "fintech_use_num") fintechUseNum: String,
        @RequestParam(name = "tran_dtime") tranDtime: String = makeTranDtime()
    ): OpenbankAccountBalanceResponse
}