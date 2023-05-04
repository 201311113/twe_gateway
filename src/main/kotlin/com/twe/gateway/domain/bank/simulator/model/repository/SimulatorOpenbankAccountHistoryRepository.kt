package com.tw2.prepaid.domain.bank.simulator.model.repository

import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType
import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankAccountEntity
import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankAccountHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SimulatorOpenbankAccountHistoryRepository: JpaRepository<SimulatorOpenbankAccountHistoryEntity, Long> {
    fun findTopByMyAccountOrderByBankTranDtimeDesc(myAccount: SimulatorOpenbankAccountEntity): SimulatorOpenbankAccountHistoryEntity?
    fun findByBankTranIdAndCheckTypeAndTranAmt(bankTranId: String, checkType: OpenbankTransferCheckType, tranAmt: Long): SimulatorOpenbankAccountHistoryEntity?
    fun deleteByMyAccountOrOtherAccount(myAccount: SimulatorOpenbankAccountEntity, otherAccount: SimulatorOpenbankAccountEntity)
}