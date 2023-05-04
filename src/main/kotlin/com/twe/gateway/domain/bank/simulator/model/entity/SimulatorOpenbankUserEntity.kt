package com.tw2.prepaid.domain.bank.simulator.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.member.model.entity.Member
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity(name = "simulator_openbank_user")
class SimulatorOpenbankUserEntity(
    @OneToOne
    @JoinColumn(name = "member_id")
    val member: Member,
    val reqClientNum: String,
): BaseEntity()