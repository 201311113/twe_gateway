package com.tw2.prepaid.domain.member

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.member.dto.request.MemberRequest
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/prepaid/v1.0/members"])
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping
    @Operation(description = "확인함")
    @Hidden
    fun getMembers(@PageableDefault pageable: Pageable) =
        Response(data = memberService.getMembers(pageable))

    @GetMapping("/{memberId}")
    @Operation(description = "확인함")
    @Hidden
    fun getMember(@PathVariable memberId: Long) =
        Response(data = memberService.getMember(memberId))

    @PutMapping("/{memberId}")
    @Hidden
    fun updateMember(@PathVariable memberId: Long, @RequestBody req: MemberRequest) =
        Response(data = memberService.updateMember(memberId = memberId, req = req))

    @DeleteMapping("/{memberId}")
    @Hidden
    fun deleteMember(@PathVariable memberId: Long): Response<Unit> {
        memberService.deleteMember(memberId = memberId)
        return Response()
    }
}