package com.tw2.prepaid.domain.bank.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType
import com.tw2.prepaid.domain.bank.model.OpenbankTransferResultEnum
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "openbank_transfer_history")
class OpenbankTransferHistory(
    val userId: Long,
    val walletId: Long,
    val fintechNum: String,
    val userSeqNum: String,
    @Enumerated(EnumType.STRING)
    val transferType: OpenbankTransferCheckType,
    @Enumerated(EnumType.STRING)
    var tranResult: OpenbankTransferResultEnum = OpenbankTransferResultEnum.DONE,
    val tranAmt: Int,
    val tranNo: Byte,
    val bankTranId: String,
    val apiTranId: String,
    val apiTranDtm: LocalDateTime,
    val userPrintContent: String,
    val userAccountNumMasked: String,
    val userBankCode: String,
    val userAccountHolderName: String,
    val partnerPrintContent: String,
    val partnerAccountNumMasked: String,
    val partnerBankCode: String,
    val partnerAccountHolderName: String,
    val rspCode: String,
    val bankRspCode: String,
    val businessUuid: String,
): BaseEntity()