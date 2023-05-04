package com.tw2.prepaid.domain.bank.simulator

import com.tw2.prepaid.common.EMPTY_MESSAGE
import com.tw2.prepaid.domain.bank.common.MILLIS_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMDT_FORMATTER
import com.tw2.prepaid.domain.bank.common.YMD_FORMATTER
import com.tw2.prepaid.domain.bank.model.*
import com.tw2.prepaid.domain.bank.model.OpenbankTransferCheckType.*
import com.tw2.prepaid.domain.bank.model.OpenbankAccountRegisterType.*
import com.tw2.prepaid.common.YnType.*
import com.tw2.prepaid.common.jpa.MASKING_STR
import com.tw2.prepaid.domain.bank.dto.request.openbank.OpenbankRefreshAccessTokenRequest
import com.tw2.prepaid.domain.bank.dto.response.openbank.OpenbankRefreshAccessTokenResponse
import com.tw2.prepaid.domain.bank.simulator.dto.*
import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankAccountEntity
import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankAccountHistoryEntity
import com.tw2.prepaid.domain.bank.simulator.model.entity.SimulatorOpenbankUserEntity
import com.tw2.prepaid.domain.bank.simulator.model.repository.SimulatorOpenbankAccountHistoryRepository
import com.tw2.prepaid.domain.bank.simulator.model.repository.SimulatorOpenbankAccountRepository
import com.tw2.prepaid.domain.bank.simulator.model.repository.SimulatorOpenbankUserRepository
import com.tw2.prepaid.domain.member.model.repository.MemberRepository
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.springframework.transaction.annotation.Transactional
const val DEFAULT_BALANCE = 100000000L
@Service
// TODO 조회, 출금 동의 YN 여부에 따른 권한 체크 에러 핸들링 필요
class SimulatorOpenbankService(
    private val accountHistoryRepository: SimulatorOpenbankAccountHistoryRepository,
    private val accountRepository: SimulatorOpenbankAccountRepository,
    private val userRepository: SimulatorOpenbankUserRepository,
    private val memberRepository: MemberRepository,
) {
    fun getTransferResult(req: SimulatorOpenbankTransferRequest): SimulatorOpenbankTransferResponse {
        lateinit var wdBankAccount: SimulatorOpenbankAccountEntity
        lateinit var dpsBankAccount: SimulatorOpenbankAccountEntity
        val resList: List<SimulatorOpenbankTransferDetailsResponse> = req.reqList.map {
            val result = accountHistoryRepository.findByBankTranIdAndCheckTypeAndTranAmt(it.orgBankTranId, req.checkType, it.orgTranAmt.toLong()) ?:
                return@map SimulatorOpenbankTransferDetailsResponse(bankRspCode = ParticipatingBankResponseCode.`701`.name)

            if (result.bankTranDtime.format(DateTimeFormatter.ISO_LOCAL_DATE).replace("-", EMPTY_MESSAGE) != it.orgBankTranDate)
                return@map SimulatorOpenbankTransferDetailsResponse(bankRspCode = ParticipatingBankResponseCode.`701`.name)

            when (req.checkType) {
                WITHDRAW -> {
                    wdBankAccount = result.myAccount
                    dpsBankAccount = result.otherAccount
                }
                DEPOSIT -> {
                    wdBankAccount = result.otherAccount
                    dpsBankAccount = result.myAccount
                }
            }
            SimulatorOpenbankTransferDetailsResponse(
                tranNo = it.tranNo,
                bankTranId = it.orgBankTranId,
                bankTranDate = it.orgBankTranDate,
                bankCodeTran = BankCode.오픈은행.code,
                bankRspCode =
                    if (wdBankAccount.isRspCodeTest)
                        wdBankAccount.bankRspCode.name
                    else if (dpsBankAccount.isRspCodeTest)
                        dpsBankAccount.bankRspCode.name
                    else ParticipatingBankResponseCode.`000`.name,
                wdBankCodeStd = wdBankAccount.bankCodeStd,
                wdBankCodeSub = wdBankAccount.bankCodeSub,
                wdBankName = wdBankAccount.bankName,
                wdFintechUseNum = wdBankAccount.fintechUseNum,
                wdAccountNumMasked = wdBankAccount.accountNumMasked,
                wdPrintContent = "TEST",
                wdAccountHolderName = wdBankAccount.accountHolderName,
                dpsBankCodeStd = dpsBankAccount.bankCodeStd,
                dpsBankCodeSub = dpsBankAccount.bankCodeSub,
                dpsBankName = dpsBankAccount.bankName,
                dpsFintechUseNum = dpsBankAccount.fintechUseNum,
                dpsAccountNumMasked = dpsBankAccount.accountNumMasked,
                dpsPrintContent = "TEST",
                tranAmt = result.tranAmt.toString()
            )
        }

        return SimulatorOpenbankTransferResponse(
            apiTranId = UUID.randomUUID().toString(),
            rspCode =
                if (wdBankAccount.isRspCodeTest)
                    wdBankAccount.apiRspCode.name
                else if (dpsBankAccount.isRspCodeTest)
                    dpsBankAccount.apiRspCode.name
                else OpenBankApiResponseCode.A0000.name,
            resList = resList,
            resCnt = resList.count().toString()
        )
    }

    @Transactional
    fun deposit(req: SimulatorOpenbankDepositRequest): SimulatorOpenbankDepositResponse {
        // 출금 대상 (ex 트래블월렛)
        val withdrawAccount = accountRepository.findByAccountNum(req.cntrAccountNum)
            // TODO 이 경우 오픈뱅킹에서의 실제 에러 처리는 어케 되지??
            ?: return SimulatorOpenbankDepositResponse(rspCode = OpenBankApiResponseCode.A0003.name)

        lateinit var myAccount: SimulatorOpenbankAccountEntity
        val reqList: List<SimulatorOpenbankDepositTargetResponse> = req.reqList.mapIndexed { i, req ->
            myAccount = accountRepository.findByFintechUseNum(req.fintechUseNum)
                // TODO 이 경우 오픈뱅킹에서의 실제 에러 처리는 어케 되지??
                ?: return@mapIndexed SimulatorOpenbankDepositTargetResponse(bankRspCode = ParticipatingBankResponseCode.`803`.name)

            val myAccountHistory = accountHistoryRepository.findTopByMyAccountOrderByBankTranDtimeDesc(myAccount)
            // 입금 프로세싱
            accountHistoryRepository.save(SimulatorOpenbankAccountHistoryEntity(
                myAccount = myAccount, otherAccount = withdrawAccount, bankTranId = req.bankTranId,
                bankCodeTran = myAccount.bankCodeStd, tranAmt = req.tranAmt.toLong(), checkType = DEPOSIT,
                balanceAmt = (myAccountHistory?.balanceAmt ?: DEFAULT_BALANCE) + req.tranAmt.toLong()
            ))

            myAccount.run { SimulatorOpenbankDepositTargetResponse(
                tranNo = (i + 1).toString(), bankTranId = req.bankTranId,
                bankCodeTran = bankCodeStd /* bankCodeTran 잘 모름 */,
                bankRspCode = if (myAccount.isRspCodeTest) myAccount.bankRspCode.name else ParticipatingBankResponseCode.`000`.name,
                fintechUseNum = fintechUseNum, accountAlias = accountAlias, bankCodeStd = bankCodeStd,
                bankCodeSub = bankCodeSub, bankName = bankName, savingsBankName = savingsBankName,
                accountNumMasked = accountNumMasked, printContent = "TEST", accountHolderName = accountHolderName, tranAmt = req.tranAmt
            ) }
        }

        if (myAccount.isRspCodeTest && myAccount.apiRspCode == OpenBankApiResponseCode.A0017)
            Thread.sleep(17000)

        return withdrawAccount.run { SimulatorOpenbankDepositResponse(
            rspCode = if (myAccount.isRspCodeTest) myAccount.apiRspCode.name else OpenBankApiResponseCode.A0000.name,
            wdBankCodeStd = bankCodeStd, wdBankCodeSub = bankCodeSub,
            wdBankName = bankName, wdAccountNumMasked = accountNumMasked, wdPrintContent = "TEST",
            wdAccountHolderName = accountHolderName, resList = reqList
        ) }
    }

    @Transactional
    fun withdraw(req: SimulatorOpenbankWithdrawRequest): SimulatorOpenbankWithdrawResponse {
        // 입금 대상 (ex 트래블월렛)
        val dpAccount = accountRepository.findByAccountNum(req.cntrAccountNum)
            // TODO 이 경우 오픈뱅킹에서의 실제 에러 처리는 어케 되지??
            ?: return SimulatorOpenbankWithdrawResponse(rspCode = OpenBankApiResponseCode.A0003.name)

        val myAccount = accountRepository.findByFintechUseNum(req.fintechUseNum)
            // TODO 이 경우 오픈뱅킹에서의 실제 에러 처리는 어케 되지??
            ?: return SimulatorOpenbankWithdrawResponse(rspCode = OpenBankApiResponseCode.A0003.name)

        val myAccountHistory = accountHistoryRepository.findTopByMyAccountOrderByBankTranDtimeDesc(myAccount)
        val balanceAmt = myAccountHistory?.balanceAmt ?: DEFAULT_BALANCE
        val tranAmt = req.tranAmt.toLong()
        if (balanceAmt < tranAmt)
            return SimulatorOpenbankWithdrawResponse(
                rspCode = OpenBankApiResponseCode.A0002.name,
                bankRspCode = ParticipatingBankResponseCode.`453`.name
            )

        // 출금 프로세싱
        accountHistoryRepository.save(SimulatorOpenbankAccountHistoryEntity(
            myAccount = myAccount, otherAccount = dpAccount, bankTranId = req.bankTranId,
            bankCodeTran = myAccount.bankCodeStd, tranAmt = tranAmt, checkType = WITHDRAW,
            balanceAmt = balanceAmt - tranAmt
        ))

        if (myAccount.isRspCodeTest && myAccount.apiRspCode == OpenBankApiResponseCode.A0017)
            Thread.sleep(17000)

        return SimulatorOpenbankWithdrawResponse(
            rspCode = if (myAccount.isRspCodeTest) myAccount.apiRspCode.name else OpenBankApiResponseCode.A0000.name,
            dpsBankCodeStd = dpAccount.bankCodeStd,
            dpsBankCodeSub = dpAccount.bankCodeSub, dpsBankName = dpAccount.bankName,
            dpsAccountNumMasked = dpAccount.accountNumMasked, dpsPrintContent = "TEST",
            dpsAccountHolderName = dpAccount.accountHolderName, bankTranId = req.bankTranId,
            bankCodeTran = myAccount.bankCodeStd, // ??
            bankRspCode = if (myAccount.isRspCodeTest) myAccount.bankRspCode.name else ParticipatingBankResponseCode.`000`.name,
            fintechUseNum = myAccount.fintechUseNum,
            accountAlias = myAccount.accountAlias, bankCodeStd = myAccount.bankCodeStd, bankCodeSub = myAccount.bankCodeSub,
            bankName = myAccount.bankName, savingsBankName = myAccount.savingsBankName, accountNumMasked = myAccount.accountNumMasked,
            printContent = "TEST", accountHolderName = myAccount.accountHolderName, tranAmt = req.tranAmt,
            wdLimitRemainAmt = "9999999"
        )
    }

    fun getAccounts(userSeqNo: String): SimulatorOpenbankAccountsResponse {
        val dummyResult = SimulatorOpenbankAccountsResponse(
            apiTranId = UUID.randomUUID().toString(),
            apiTranDtm = LocalDateTime.now().format(MILLIS_FORMATTER),
            rspCode = OpenBankApiResponseCode.A0000.name,
            resList = emptyList(),
            resCnt = 0.toString()
        )

        val openbankingUser = userRepository.findByReqClientNum(userSeqNo) ?: return dummyResult
        val resList = accountRepository.findByMember(openbankingUser.member).map {
            SimulatorOpenbankAccount(
                fintechUseNum = it.fintechUseNum,
                accountAlias = it.accountAlias,
                bankCodeStd = it.bankCodeStd,
                bankCodeSub = it.bankCodeSub,
                bankName = it.bankName,
                savingsBankName = it.savingsBankName,
                accountNum = it.accountNum,
                accountNumMasked = it.accountNumMasked,
                accountSeq = it.accountSeq,
                accountHolderName = it.accountHolderName,
                accountHolderType = it.accountHolderType,
                accountType = it.accountType,
                inquiryAgreeYn = it.inquiryAgreeYn,
                inquiryAgreeDtime = it.inquiryAgreeDtime.format(YMDT_FORMATTER),
                transferAgreeYn = it.transferAgreeYn,
                transferAgreeDtime = it.transferAgreeDtime.format(YMDT_FORMATTER),
                accountState = it.accountState,
            )
        }

        return dummyResult.copy(resList = resList, resCnt = resList.count().toString())
    }

    fun getAccountBalance(bankTranId: String = UUID.randomUUID().toString(), fintechUseNum: String): SimulatorOpenbankAccountBalanceResponse {
        val myAccount = accountRepository.findByFintechUseNum(fintechUseNum) ?:
            // TODO 이 경우 오픈뱅킹에서의 실제 에러 처리는 어케 되지??
            return SimulatorOpenbankAccountBalanceResponse(rspCode = OpenBankApiResponseCode.A0003.name)

        val accountHistory = accountHistoryRepository.findTopByMyAccountOrderByBankTranDtimeDesc(myAccount) ?:
            accountHistoryRepository.save(SimulatorOpenbankAccountHistoryEntity( // 의미 없는 dummy 값
                myAccount = myAccount, otherAccount = myAccount, bankTranId = bankTranId,
                bankCodeTran = myAccount.bankCodeStd, tranAmt = 10000 /* 의미 없음 */, checkType = DEPOSIT,
                balanceAmt = DEFAULT_BALANCE
            ))
        val balanceAmt = accountHistory.balanceAmt.toString()
        return SimulatorOpenbankAccountBalanceResponse(
            rspCode = if (myAccount.isRspCodeTest) myAccount.apiRspCode.name else OpenBankApiResponseCode.A0000.name, bankTranId = bankTranId,
            bankCodeTran = myAccount.bankCodeStd, // ??
            bankRspCode = if (myAccount.isRspCodeTest) myAccount.bankRspCode.name else ParticipatingBankResponseCode.`000`.name,
            fintechUseNum = myAccount.fintechUseNum,
            bankName = myAccount.bankName, balanceAmt = balanceAmt, availableAmt = balanceAmt,
            accountType = OpenbankAccountType.수시입출금.code.toString(), accountIssueDate = myAccount.createdAt.format(YMD_FORMATTER),
            maturityDate = "00000000", lastTranDate = accountHistory.createdAt.format(YMD_FORMATTER),
        )
    }
    @Transactional
    fun registerAccount(req: SimulatorOpenbankRegisterAccountRequest): SimulatorOpenbankRegisterAccountResponse {
        // TODO
        val member = memberRepository.findByCi(req.userCi) ?: return SimulatorOpenbankRegisterAccountResponse(rspCode = OpenBankApiResponseCode.A0003.name)

        // 계좌 등록
        val accountEntity = accountRepository.findByMember(member).find { it.accountNum == req.registerAccountNum && it.bankCodeStd == req.bankCodeStd }
            ?: SimulatorOpenbankAccountEntity(member = member, bankCodeStd = req.bankCodeStd, transferAgreeYn = N,
                accountHolderName = member.name ?: EMPTY_MESSAGE,
                accountNumMasked = req.registerAccountNum.replaceRange(0,req.registerAccountNum.length - 3, MASKING_STR),
                inquiryAgreeYn = N, accountNum = req.registerAccountNum, fintechUseNum = RandomStringUtils.randomAlphanumeric(24).uppercase())

        when (req.scope) {
            transfer -> {
                accountEntity.transferAgreeYn = Y
                accountEntity.transferAgreeDtime = LocalDateTime.now()
            }
            inquiry -> {
                accountEntity.inquiryAgreeYn = Y
                accountEntity.inquiryAgreeDtime = LocalDateTime.now()
            }
        }
        val myAccount = accountRepository.save(accountEntity)

        // 오픈뱅킹 유저가 없을 경우 유저 등록
        val openbankingUser = userRepository.findByMember(member)
        val userSeqNo = openbankingUser?.reqClientNum ?: RandomStringUtils.randomAlphanumeric(10).uppercase()
        if (openbankingUser == null) {
            userRepository.save(SimulatorOpenbankUserEntity(member = member, reqClientNum = userSeqNo))
        }

        // 잔액 정보가 없는 신규 사용자일경우 일괄적으로 가라 잔액으로 시작하게 함
        if (accountHistoryRepository.findTopByMyAccountOrderByBankTranDtimeDesc(myAccount) == null) {
            val twAccount = accountRepository.findAll().find { it.accountHolderName == "(주)트래블월렛" }!!
            accountHistoryRepository.save(SimulatorOpenbankAccountHistoryEntity(
                myAccount = myAccount, otherAccount = twAccount, bankTranId = req.bankTranId,
                balanceAmt = DEFAULT_BALANCE, tranAmt = 100000, checkType = DEPOSIT
            ))
        }

        return SimulatorOpenbankRegisterAccountResponse(
            rspCode = OpenBankApiResponseCode.A0000.name, bankTranId = req.bankTranId,
            bankCodeTran = req.bankCodeStd, bankRspCode = ParticipatingBankResponseCode.`000`.name,
            bankName = getBankName(req.bankCodeStd), userSeqNo = userSeqNo, fintechUseNum = myAccount.fintechUseNum,
            transferBankTranId = req.bankTranId
        )
    }

    @Transactional
    fun cancelAccount(req: SimulatorOpenbankCancelAccountRequest): SimulatorOpenbankCancelAccountResponse {
        // TODO
        val account = accountRepository.findByFintechUseNum(req.fintechUseNum) ?:
            return SimulatorOpenbankCancelAccountResponse(rspCode = OpenBankApiResponseCode.A0000.name)

        when (req.scope) {
            transfer -> {
                account.transferAgreeYn = N
                account.transferAgreeDtime = LocalDateTime.now()
            }
            inquiry -> {
                account.inquiryAgreeYn = N
                account.inquiryAgreeDtime = LocalDateTime.now()
            }
        }

        if (account.transferAgreeYn == N && account.inquiryAgreeYn == N) {
            accountHistoryRepository.deleteByMyAccountOrOtherAccount(account, account)
            accountRepository.delete(account)
        }

        return SimulatorOpenbankCancelAccountResponse(
            rspCode = OpenBankApiResponseCode.A0000.name, bankTranId = req.bankTranId,
            bankRspCode = ParticipatingBankResponseCode.`000`.name,
            bankCodeTran = account.bankCodeStd
        )
    }
    @Transactional
    fun closeUser(req: SimulatorOpenbankCloseUserRequest): SimulatorOpenbankCloseUserResponse {
        val obUser = userRepository.findByReqClientNum(req.userSeqNo) ?: return SimulatorOpenbankCloseUserResponse(rspCode = OpenBankApiResponseCode.A0000.name)
        accountRepository.findByMember(obUser.member).forEach {
            accountHistoryRepository.deleteByMyAccountOrOtherAccount(it, it)
        }
        accountRepository.deleteByMember(obUser.member)
        userRepository.delete(obUser)
        return SimulatorOpenbankCloseUserResponse(rspCode = OpenBankApiResponseCode.A0000.name)
    }
    fun refreshAccessToken(req: OpenbankRefreshAccessTokenRequest) = OpenbankRefreshAccessTokenResponse(
        accessToken = "TEST-PREFIX : ${UUID.randomUUID()}",
        tokenType = "Bearer",
        expiresIn = 7776000,
        scope = "sa",
        clientUseCode = "B001234560",
    )
}