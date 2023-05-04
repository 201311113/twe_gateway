package com.tw2.prepaid.domain.wallet.service

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.common.support.annotation.PrimaryDb
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType
import com.tw2.prepaid.domain.bank.model.entity.OpenbankAccount
import com.tw2.prepaid.domain.bank.model.getBankName
import com.tw2.prepaid.domain.bank.model.repository.OpenbankAccountRepository
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankDepositTargetRequest
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankRegisterAccountRequest
import com.tw2.prepaid.domain.bank.dto.response.BankAccount
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.member.model.UserStatusType
import com.tw2.prepaid.domain.wallet.dto.request.WalletAccountRegisterRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletAccountRequest
import com.tw2.prepaid.domain.wallet.dto.request.WalletPennyAuthRequest
import com.tw2.prepaid.domain.wallet.dto.response.WalletAccountResponse
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.entity.WalletAccount
import com.tw2.prepaid.domain.wallet.model.repository.WalletAccountRepository
import com.tw2.prepaid.domain.wallet.model.repository.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class WalletAccountService(
    private val wcs: WalletCommonService,
    private val obService: OpenbankService,
    private val obAccountRepository: OpenbankAccountRepository,
    private val walletRepository: WalletRepository,
    private val walletAccountRepository: WalletAccountRepository,
    private val pp: PrepaidProperties,
) {
    @PrimaryDb // transaction 을 전체로 걸지 않았음 (자주쓰는 메소드라)
    fun getCurrentAccounts(walletId: Long, useDbAccount: Boolean = false): List<BankAccount> {
        val wallet = wcs.getWalletNoLocking(walletId)
        var mainFintechNum = wallet.mainFintechNum
        // db 상 관리하는 계좌 리스트
        val dbAccounts = walletAccountRepository.findByWallet(wallet = wallet)
        val dbMainFintechNum = dbAccounts.find { it.isMain }?.accountId
        // 실제 오픈뱅킹으로 부터 가져온 계좌 리스트
        val obAccounts = obService.getAccounts(walletId = walletId)
        val activeObAccounts = obAccounts.filter { it.isActiveAccount }
        val inactiveObAccounts = obAccounts.filterNot { it.isActiveAccount }

        // 메인계좌 후보도 없는 케이스
        if (activeObAccounts.isEmpty())
            throw DefaultException(errorCode = ErrorCode.NOT_EXIST_TARGET_ACCOUNT)
        // 메인계좌 일치하지 않아 교체1 (1.0 스타일로 wallet 에 있는 main 계좌 변경)
        if (activeObAccounts.all { wallet.mainFintechNum != it.fintechUseNum }) {
            val candidateAccount = activeObAccounts[0]
            wallet.updateMainAccount(
                mainFintechNum = candidateAccount.fintechUseNum,
                mainAccountNum = candidateAccount.accountNumMasked,
                mainBankStdCode = candidateAccount.bankCodeStd
            )
            walletRepository.save(wallet)
            mainFintechNum = candidateAccount.fintechUseNum
        }
        // db 상 관리하는 계좌 중에서 실제 오픈뱅킹 계좌 조회에 없는 것들 제거
        val dbFintechNums = dbAccounts.map(WalletAccount::accountId)
        val garbageAccounts = dbFintechNums.subtract(activeObAccounts.map(BankAccount::fintechUseNum).toSet()) +
                    dbFintechNums.intersect(inactiveObAccounts.map(BankAccount::fintechUseNum).toSet())
        if (garbageAccounts.isNotEmpty()) {
            walletAccountRepository.deleteByWalletAndAccountIdIn(wallet = wallet, accountIds = garbageAccounts)
        }
        // 메인계좌 일치하지 않아 교체2 (db 상 관리하는 계좌)
        val activeDbAccounts = dbAccounts.filterNot { garbageAccounts.contains(it.accountId) }
        if (activeObAccounts.all { dbMainFintechNum != it.fintechUseNum }) {
            val candidateMainDbAccount = activeDbAccounts.firstOrNull()
            activeDbAccounts.forEach {
                it.isMain = false
            }
            candidateMainDbAccount?.isMain = true
            candidateMainDbAccount?.let { walletAccountRepository.save(it) }
            walletAccountRepository.saveAll(activeDbAccounts)
        }

        val realDbAccountIds = activeDbAccounts.map(WalletAccount::accountId).toSet()
        return activeObAccounts
            .filter { if (useDbAccount) realDbAccountIds.contains(it.fintechUseNum) else true }
            .map {
                if (it.isAvailable) {
                    if (useDbAccount && dbMainFintechNum == it.fintechUseNum)
                        it.isMain = true
                    if (!useDbAccount && mainFintechNum == it.fintechUseNum)
                        it.isMain = true
                }
                it
            }
    }
    @PrimaryDb
    fun getCurrentAccount(walletId: Long, fintechUseNum: String): BankAccount {
        val accounts = getCurrentAccounts(walletId)
        return accounts.find { it.fintechUseNum == fintechUseNum } ?: throw DefaultException(errorCode = ErrorCode.NOT_EXIST_TARGET_ACCOUNT)
    }
    @Transactional
    fun pennySuccessCallback(walletId: Long, req: WalletAccountRequest) {
        val wallet = wcs.getWalletNoLocking(walletId)
        val user = wallet.user
        val member = user.member

        val result = obService.registerAccount(
            OpenbankRegisterAccountRequest(
                bankCodeStd = req.bankCodeStd,
                registerAccountNum = req.accountNum,
                userInfo = member.birthday?.format(YMD_FORMATTER) ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND),
                userName = member.name ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND),
                userEmail = req.email,
                userCi = member.ci,
                scope = OpenbankAccountRegisterType.inquiry
            )
        )
        val walletAccount = walletAccountRepository.findByWalletAndAccountId(accountId = result.fintechUseNum, wallet = wallet)
            ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND, message = "출금동의가 선행되지 않았습니다.")
        walletAccount.isMain = !walletAccountRepository.existsByWalletAndIsMain(wallet = wallet, isMain = true)

        val obAccount = obAccountRepository.findByFintechUseNum(fintechUseNum = result.fintechUseNum)
            ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND, message = "출금동의가 선행되지 않았습니다.")
        obAccount.isInquiryAgree = true
        obAccount.inquiryAgreeDt = LocalDateTime.now()

        // 메인계좌가 없거나 메인계좌 교체요청일 경우엔 저장
        if (wallet.mainFintechNum == null)
            updateMainAccount(wallet, req)

        user.registerAccount(result.userSeqNo)
        user.status = UserStatusType.VERIFIED
        member.email = req.email
    }
    // No Transactional
    @PrimaryDb
    fun pennyAuthentication(walletId: Long, req: WalletPennyAuthRequest) {
        val wallet = wcs.getWalletNoLocking(walletId)
        // 1원 입금, 오픈뱅킹 (처리중, readTimeout)의 애매한 장애케이스도 성공으로 처리된다.
        obService.deposit(
            walletId = walletId, userId = wallet.user.id, useMainAccount = false,
            businessUuid = UUID.randomUUID().toString(),
            originReq = OpenbankDepositRequest(
                wdPassPhrase = pp.getSecretValue(SecretKey.OB_PASS_PHRASE), // 토큰으로서 상수로 갖고 있어야 함
                wdPrintContent = req.twContent, // 트래블월렛 계좌에 어떻게 보여줄지에 대한 인자
                reqList = listOf(
                    OpenbankDepositTargetRequest(
                        tranNo = "1",
                        fintechUseNum = req.fintechUseNum,
                        reqClientFintechUseNum = req.fintechUseNum,
                        printContent = req.userContent, // 사용자의 계좌에 어떻게 보여줄지에 대한 인자
                        tranAmt = "1",
                        reqClientName = wallet.user.member.name ?: EMPTY_MESSAGE,
                        reqClientNum = req.userSeqNum,
                    )
                )
            )
        )
    }
    @Transactional
    fun unregisterAccount(walletId: Long, fintechUseNum: String) {
        val wallet = wcs.getWalletNoLocking(walletId)
        val walletAccounts = walletAccountRepository.findByAccountId(accountId = fintechUseNum)
        walletAccounts.find { it.accountId == fintechUseNum }?.let {
            walletAccountRepository.deleteByWalletAndAccountIdIn(wallet = wallet, accountIds = setOf(it.accountId))
        }
        if (wallet.mainFintechNum == fintechUseNum) {
            wallet.updateMainAccount(mainFintechNum = null, mainAccountNum = null, mainBankStdCode = null)
        }
        if (walletAccounts.size < 2) {
            obAccountRepository.deleteByFintechUseNum(fintechUseNum)
            obService.unregisterAccount(fintechUseNum)
        }
    }
    @Transactional
    fun registerAccount(walletId: Long, req: WalletAccountRegisterRequest): Response<WalletAccountResponse> {
        val wallet = wcs.getWalletNoLocking(walletId)
        val member = wallet.user.member
        val result = obService.registerAccount(
            OpenbankRegisterAccountRequest(
                bankCodeStd = req.bankCodeStd,
                registerAccountNum = req.accountNum,
                userInfo = req.birthday.format(YMD_FORMATTER),
                userName = member.name ?: EMPTY_MESSAGE,
                userEmail = member.email,
                userCi = member.ci,
                scope = OpenbankAccountRegisterType.transfer,
                clientDeviceType = req.clientDeviceType,
                clientDeviceIp = req.clientDeviceIp,
                clientDeviceId = req.clientDeviceId,
                clientDeviceNum = req.clientDeviceNum,
                clientDeviceVersion = req.clientDeviceVersion
            )
        )
        wallet.user.registerAccount(result.userSeqNo)
        walletAccountRepository.findByWalletAndAccountId(accountId = result.fintechUseNum, wallet = wallet) ?:
            walletAccountRepository.save(
                WalletAccount(
                    wallet = wallet,
                    accountId = result.fintechUseNum,
                    isMain = false
                )
            )
        val obAccount = obAccountRepository.findByFintechUseNum(fintechUseNum = result.fintechUseNum)
        obAccountRepository.save(
            obAccount?.also {
                it.isTransferAgree = true
                it.transferAgreeDt = LocalDateTime.now()
            } ?: OpenbankAccount(
                fintechUseNum = result.fintechUseNum,
                bankCodeStd = req.bankCodeStd,
                accountNum = req.accountNum,
                isInquiryAgree = false,
                transferAgreeDt = LocalDateTime.now(),
                isTransferAgree = true,
            )
        )
        return Response(
            data = WalletAccountResponse(
                fintechNum = result.fintechUseNum,
                userSeqNum = result.userSeqNo,
                bankStdCode = req.bankCodeStd,
                bankName = getBankName(req.bankCodeStd),
                accountNum = req.accountNum
            )
        )
    }
    // No transactional
    @PrimaryDb
    fun changeMainAccount(walletId: Long, req: WalletAccountRequest) {
        updateMainAccount(wcs.getWalletNoLocking(walletId), req)
    }
    private fun updateMainAccount(wallet: Wallet, req: WalletAccountRequest) {
        wallet.updateMainAccount(
            mainFintechNum = req.fintechUseNum,
            mainAccountNum = req.accountNum,
            mainBankStdCode = req.bankCodeStd
        )
        walletRepository.save(wallet)
    }
}