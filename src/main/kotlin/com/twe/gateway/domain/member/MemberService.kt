package com.tw2.prepaid.domain.member

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.domain.member.dto.request.MemberRequest
import com.tw2.prepaid.domain.member.dto.response.MemberResponse
import com.tw2.prepaid.domain.member.model.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun getMembers(pageable: Pageable): Page<MemberResponse> =
        memberRepository.findAll(pageable).map(MemberResponse::createFromEntity)

    fun getMember(memberId: Long) = MemberResponse.createFromEntity(getMemberInner(memberId))

    @Transactional
    fun updateMember(memberId: Long, req: MemberRequest): MemberResponse {
        val member = getMemberInner(memberId)
        req.updateEntity(member)
        return MemberResponse.createFromEntity(member)
    }

    fun deleteMember(memberId: Long) = memberRepository.deleteById(memberId)

    private fun getMemberInner(memberId: Long) = memberRepository.findByIdOrNull(memberId)
        ?: throw DefaultException(message = "member is not found.")

}