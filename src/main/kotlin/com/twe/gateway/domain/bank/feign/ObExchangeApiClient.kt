package com.tw2.prepaid.domain.bank.feign

import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankWithdrawRequest
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankDepositResponse
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankWithdrawResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

const val WITHDRAW_PATH = "/v2.0/transfer/withdraw/fin_num"
const val DEPOSIT_PATH = "/v2.0/transfer/deposit/fin_num"

@FeignClient(
    url = "\${external.openbank-api.url}",
    value = "ob-exchange-api",
    configuration = [OpenbankApiConfiguration::class]
)
@Validated
interface ObExchangeApiClient {
    // 고객 출금시 처리중에러 or Timeout 은 실패로 처리
    @PostMapping(WITHDRAW_PATH)
    fun withdraw(@Valid @RequestBody req: OpenbankWithdrawRequest): OpenbankWithdrawResponse
    // 고객 입금시 처리중에러 or Timeout 은 성공으로 처리
    @PostMapping(DEPOSIT_PATH)
    fun deposit(@Valid @RequestBody req: OpenbankDepositRequest): OpenbankDepositResponse
}