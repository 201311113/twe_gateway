package com.tw2.prepaid.domain.bank.simulator.model.entity

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.YnType
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.bank.model.*
import com.tw2.prepaid.domain.member.model.entity.Member
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "simulator_openbank_account")
class SimulatorOpenbankAccountEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    val fintechUseNum: String,
    val accountAlias: String = EMPTY_MESSAGE,
    val bankCodeStd: String,
    val bankCodeSub: String = EMPTY_MESSAGE,
    val bankName: String = getBankName(bankCodeStd),
    val savingsBankName: String = EMPTY_MESSAGE,
    val accountNum: String,
    val accountNumMasked: String = EMPTY_MESSAGE,
    val accountSeq: String = EMPTY_MESSAGE,
    val accountHolderName: String = EMPTY_MESSAGE,
    val accountHolderType: Char = PERSONAL_ACCOUNT_HOLDER_TYPE,
    val accountType: Char = OpenbankAccountType.수시입출금.code,
    @Enumerated(EnumType.STRING)
    var inquiryAgreeYn: YnType = YnType.N,
    var inquiryAgreeDtime: LocalDateTime = LocalDateTime.now(), // 14자리
    @Enumerated(EnumType.STRING)
    var transferAgreeYn: YnType = YnType.N,
    var transferAgreeDtime: LocalDateTime = LocalDateTime.now(), // 14자리
    @Enumerated(EnumType.STRING)
    val accountState: OpenbankAccountStateType = OpenbankAccountStateType.USE,
    val isRspCodeTest: Boolean = false,
    @Enumerated(EnumType.STRING)
    val apiRspCode: OpenBankApiResponseCode = OpenBankApiResponseCode.A0000,
    @Enumerated(EnumType.STRING)
    val bankRspCode: ParticipatingBankResponseCode = ParticipatingBankResponseCode.`000`
): BaseEntity()