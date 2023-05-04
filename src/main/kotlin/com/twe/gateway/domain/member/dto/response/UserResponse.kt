package com.tw2.prepaid.domain.member.dto.response

import com.tw2.prepaid.domain.member.model.GenderType
import com.tw2.prepaid.domain.member.model.OSType
import com.tw2.prepaid.domain.member.model.entity.Member
import java.time.LocalDate

data class UserResponse(
    val memberId: Long,
    val userId: Long,
    val walletId: Long,
    val ci: String,
    val name: String?,
    val country: String?,
    val birthday: LocalDate?,
    val gender: GenderType?,
    val email: String?,
    val partners: List<PartnerResponse> = emptyList()
) {
    companion object {
        fun createFromEntity(entity: Member, walletId: Long,
                             userId: Long, partners: List<PartnerResponse> = emptyList()) =
            entity.run {
                UserResponse(
                    memberId = entity.id,
                    userId = userId,
                    partners = partners,
                    walletId = walletId,
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