package com.tw2.prepaid.domain.member.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.member.model.UserStatusType
import javax.persistence.*

@Entity(name = "partner_member")
class PartnerMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    val partner: Partner,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatusType = UserStatusType.UNVERIFIED,
    userSeqNum: String? = null
): BaseEntity() {
    // 오픈 뱅킹에서 관리하는 CI 별 고유값, 한번 세팅되면 변하지 않는 값
    var userSeqNum = userSeqNum
        protected set

    fun registerAccount(userSeqNum: String) { this.userSeqNum = userSeqNum }
}