package com.tw2.prepaid.domain.member.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.member.model.GenderType
import java.time.LocalDate
import javax.persistence.*

@Entity(name = "member")
class Member(
    @Column(unique = true, nullable = false)
    val ci: String,
    var name: String? = null,
    var country: String? = null,
    var birthday: LocalDate? = null,
    @Enumerated(EnumType.STRING)
    val gender: GenderType? = null,
    var email: String? = null,
    @OneToMany(mappedBy = "member", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val partnerMembers: MutableList<PartnerMember> = mutableListOf(),
): BaseEntity() {
    fun addPartnerMember(partnerMember: PartnerMember) = partnerMembers.add(partnerMember)
}