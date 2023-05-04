package com.tw2.prepaid.domain.bank.model.entity

import com.tw2.prepaid.common.jpa.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "openbank_account")
// TODO 필요하면 inquiry, transfer 동의 여부도 저장
// TODO 쓸지 안쓸지 모르겠네
class OpenbankAccount(
    val fintechUseNum: String,
    val bankCodeStd: String,
    val accountNum: String,
    var isInquiryAgree: Boolean,
    var inquiryAgreeDt: LocalDateTime = LocalDateTime.now(),
    var isTransferAgree: Boolean,
    var transferAgreeDt: LocalDateTime = LocalDateTime.now(),
    var isActiveAccount: Boolean = true,
): BaseEntity()