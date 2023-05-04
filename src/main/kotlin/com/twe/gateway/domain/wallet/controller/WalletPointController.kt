package com.tw2.prepaid.domain.wallet.controller

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.wallet.service.WalletPointService
import com.tw2.prepaid.domain.wallet.dto.request.WalletPointCreateRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPointRefundRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPointUseRequest
import com.tw2.prepaid.domain.wallet.dto.response.WalletValidPointResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/prepaid/v1.0/wallets/{walletId}/points"])
class WalletPointController(
    private val walletPointService: WalletPointService
) {
    @GetMapping
    fun getWalletPoints(
        @PathVariable walletId: Long,
    ): Response<WalletValidPointResponse> =
        Response(data = walletPointService.getWalletPoint(walletId = walletId))
    @PostMapping("/refund")
    fun refundWalletPoints(
        @PathVariable walletId: Long,
        @RequestBody body: WalletPointRefundRequest,
    ): Response<WalletValidPointResponse> = walletPointService.refundWalletPoints(walletId = walletId, req = body)
    @PostMapping
    fun createWalletPoint(
        @PathVariable walletId: Long,
        @RequestBody body: WalletPointCreateRequest
    ): Response<Unit> {
        walletPointService.createWalletPoint(walletId = walletId, req = body)
        // 포인트 생성 응답 결과는 따로 필요없을듯
        return Response()
    }
    @PutMapping
    fun useWalletPoint(
        @PathVariable walletId: Long,
        @RequestBody body: WalletPointUseRequest
    ): Response<WalletValidPointResponse> = Response(
        data = walletPointService.useWalletPointFromReq(walletId = walletId, req = body)
    )
}