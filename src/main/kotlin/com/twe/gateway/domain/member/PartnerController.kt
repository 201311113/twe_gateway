package com.tw2.prepaid.domain.member

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.member.dto.request.PartnerRequest
import com.tw2.prepaid.domain.member.dto.request.UserRequest
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/partners"])
class PartnerController(
    private val partnerService: PartnerService,
    private val userService: UserService
) {
    @GetMapping
    fun getPartners(@PageableDefault pageable: Pageable) =
        Response(data = partnerService.getPartners(pageable))

    @GetMapping("/{partnerId}")
    fun getPartner(@PathVariable partnerId: Long) =
        Response(data = partnerService.getPartner(partnerId))

    @PostMapping
    fun createPartner(@RequestBody req: PartnerRequest) =
        Response(data = partnerService.createPartner(req))

    @PutMapping("/{partnerId}")
    fun updatePartner(@PathVariable partnerId: Long, @RequestBody req: PartnerRequest) =
        Response(data = partnerService.updatePartner(partnerId = partnerId, req = req))

    @DeleteMapping("/{partnerId}")
    fun deletePartner(@PathVariable partnerId: Long): Response<Unit> {
        partnerService.deletePartner(partnerId = partnerId)
        return Response()
    }

    @GetMapping("/{partnerId}/users")
    fun getUsers(@PathVariable partnerId: Long, @PageableDefault pageable: Pageable) =
        Response(data = userService.getUsers(partnerId, pageable))

    @PostMapping("/{partnerId}/users")
    fun createUser(@PathVariable partnerId: Long, @RequestBody req: UserRequest) =
        Response(data = userService.createUser(partnerId, req))
}