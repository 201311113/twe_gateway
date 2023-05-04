package com.tw2.prepaid.domain.member.dto.request

import com.tw2.prepaid.domain.member.model.GenderType
import com.tw2.prepaid.domain.member.model.entity.Member
import org.apache.commons.lang3.StringUtils
import java.time.LocalDate

data class UserRequest(
    val ci: String,
    val name: String? = null,
    val country: String? = null,
    val birthday: LocalDate? = null,
    val gender: GenderType? = null,
    val email: String? = null,
) {
    fun toEntity(): Member = Member(
        ci = ci,
        name = name,
        country = country,
        birthday = birthday,
        gender = gender,
        email = email,
    )

    fun updateEntity(member: Member) = member.also {
        it.name = if (StringUtils.isEmpty(name)) it.name else name
        it.email = if (StringUtils.isEmpty(email)) it.email else email
    }
}