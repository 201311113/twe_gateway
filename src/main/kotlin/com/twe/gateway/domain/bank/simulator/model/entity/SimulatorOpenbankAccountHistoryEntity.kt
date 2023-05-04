package com.tw2.prepaid.domain.bank.simulator.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.bank.model.BankCode
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "simulator_openbank_account_history")
class SimulatorOpenbankAccountHistoryEntity(
    @ManyToOne
    @JoinColumn(name = "my_openbank_account_id")
    val myAccount: SimulatorOpenbankAccountEntity,
    @ManyToOne
    @JoinColumn(name = "other_openbank_account_id")
    val otherAccount: SimulatorOpenbankAccountEntity,
    val bankTranId: String,
    val bankTranDtime: LocalDateTime = LocalDateTime.now(),
    val bankCodeTran: String = BankCode.오픈은행.code,
    val balanceAmt: Long,             // 계좌잔액(음수가능)
    val tranAmt: Long,
    @Enumerated(EnumType.STRING)
    val checkType: OpenbankTransferCheckType,
): BaseEntity()