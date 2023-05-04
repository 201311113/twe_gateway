package com.tw2.prepaid.domain.member.dto.response

import com.tw2.prepaid.domain.member.model.GenderType
import com.tw2.prepaid.domain.member.model.OSType
import com.tw2.prepaid.domain.member.model.entity.Member
import java.time.LocalDate

data class MemberResponse(
    val memberId: Long,
    val walletId: Long?,
    val userId: Long?,
    val ci: String,
    val name: String?,
    val country: String?,
    val birthday: LocalDate?,
    val gender: GenderType?,
    val email: String?,
) {
    companion object {
        fun createFromEntity(entity: Member, walletId: Long? = null, userId: Long? = null) = entity.run {
            MemberResponse(
                memberId = id,
                walletId = walletId,
                userId = userId,
                ci = ci,
                name = name,
                country = country,
                birthday = birthday,
                gender = gender,
                email = email,
            )
        }
    }
}