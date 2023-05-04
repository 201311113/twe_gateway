package com.tw2.prepaid.domain.wallet.model.entity

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.domain.member.model.entity.PartnerMember
import com.tw2.prepaid.domain.wallet.model.WalletStatusType
import javax.persistence.*

@Entity(name = "wallet")
class Wallet(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    val user: PartnerMember,
    @OneToMany(mappedBy = "wallet", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val walletAccounts: MutableList<WalletAccount> = mutableListOf(),
    @OneToMany(mappedBy = "wallet")
    val pockets: List<WalletPocket> = emptyList(),
    @Enumerated(EnumType.STRING)
    var status: WalletStatusType = WalletStatusType.NORMAL,

    mainFintechNum: String? = null,
    mainBankStdCode: String? = null,
    mainAccountNum: String? = null,
): BaseEntity() {
    var mainFintechNum: String? = mainFintechNum
        protected set
    var mainBankStdCode: String? = mainBankStdCode
        protected set
    var mainAccountNum: String? = mainAccountNum
        protected set

    fun getNotNullMainFintechNum() = mainFintechNum ?: throw DefaultException(errorCode = ErrorCode.NOT_EXIST_MAIN_ACCOUNT)
    fun getNotNullMainFintechNum2() = walletAccounts.firstOrNull(WalletAccount::isMain)?.accountId
        ?: throw DefaultException(errorCode = ErrorCode.NOT_EXIST_MAIN_ACCOUNT)
    fun getUserSeqNo() = user.userSeqNum ?: throw DefaultException(errorCode = ErrorCode.NOT_EXIST_MAIN_ACCOUNT)
    fun updateMainAccount(mainFintechNum: String?, mainBankStdCode: String?, mainAccountNum: String?) {
        this.mainFintechNum = mainFintechNum
        this.mainBankStdCode = mainBankStdCode
        this.mainAccountNum = mainAccountNum
    }
}
