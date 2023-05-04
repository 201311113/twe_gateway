package com.tw2.prepaid.domain.member

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.domain.bank.service.OpenbankService
import com.tw2.prepaid.domain.member.dto.request.UserRequest
import com.tw2.prepaid.domain.member.dto.response.PartnerResponse
import com.tw2.prepaid.domain.member.dto.response.UserResponse
import com.tw2.prepaid.domain.member.model.UserStatusType
import com.tw2.prepaid.domain.member.model.entity.PartnerMember
import com.tw2.prepaid.domain.member.model.repository.MemberRepository
import com.tw2.prepaid.domain.member.model.repository.PartnerMemberRepository
import com.tw2.prepaid.domain.member.model.repository.PartnerRepository
import com.tw2.prepaid.domain.wallet.model.WalletStatusType
import com.tw2.prepaid.domain.wallet.model.entity.Wallet
import com.tw2.prepaid.domain.wallet.model.repository.WalletRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val memberRepository: MemberRepository,
    private val partnerRepository: PartnerRepository,
    private val partnerMemberRepository: PartnerMemberRepository,
    private val walletRepository: WalletRepository,
    private val openbankService: OpenbankService,
) {
    @Transactional
    fun createUser(partnerId: Long, req: UserRequest): UserResponse {
        val partner = partnerRepository.findByIdOrNull(partnerId) ?:
            throw DefaultException(errorCode = ErrorCode.PARTNER_INFO_NOT_FOUND)
        var member = memberRepository.findByCi(req.ci)
        val user: PartnerMember
        val walletId: Long
        if (member == null) {
            member = memberRepository.save(req.toEntity())
            user = partnerMemberRepository.save(PartnerMember(partner = partner, member = member))
            walletId = walletRepository.save(Wallet(user = user)).id
        } else {
            // 탈퇴했거나 존재하지 않으면 새로 생성
            val users = partnerMemberRepository.findByMemberAndPartnerOrderByCreatedAtDesc(member, partner)
            user = if (users.all { it.status == UserStatusType.RETIRED }) partnerMemberRepository.save(PartnerMember(partner = partner, member = member))
                   else users.firstOrNull { it.status != UserStatusType.RETIRED } ?: throw DefaultException(errorCode = ErrorCode.INTERNAL_DATA_INTEGRITY)

            walletId = walletRepository.findByUser(user)?.id ?: walletRepository.save(Wallet(user = user)).id
            req.updateEntity(member)
        }
        return UserResponse.createFromEntity(entity = member, userId = user.id, walletId = walletId)
    }
    fun getUsers(partnerId: Long, pageable: Pageable): Page<UserResponse> {
        val partner = partnerRepository.findByIdOrNull(partnerId) ?:
            throw DefaultException(errorCode = ErrorCode.PARTNER_INFO_NOT_FOUND)
        // TODO fetch join 으로 한번에 가져오도록
        return partnerMemberRepository.findByPartner(partner, pageable).map {
            val wallet = walletRepository.findByUser(it) ?: throw DefaultException(errorCode = ErrorCode.WALLET_NOT_FOUND)
            UserResponse.createFromEntity(entity = it.member, userId = it.id, walletId = wallet.id)
        }
    }
    fun getUser(userId: Long): UserResponse {
        val user = partnerMemberRepository.findByIdOrNull(userId)
            ?: throw DefaultException(errorCode = ErrorCode.USER_NOT_FOUND)
        val wallet = walletRepository.findByUser(user) ?: throw DefaultException(errorCode = ErrorCode.WALLET_NOT_FOUND)
        val partners = user.member.partnerMembers.map { PartnerResponse.createFromEntity(it.partner) }

        return UserResponse.createFromEntity(entity = user.member, userId = userId,
            walletId = wallet.id, partners = partners)
    }
    @Transactional
    fun updateUser(userId: Long, req: UserRequest): UserResponse {
        val user = partnerMemberRepository.findByIdOrNull(userId)
            ?: throw DefaultException(errorCode = ErrorCode.USER_NOT_FOUND)
        val wallet = walletRepository.findByUser(user) ?: throw DefaultException(errorCode = ErrorCode.WALLET_NOT_FOUND)
        req.updateEntity(user.member)
        return UserResponse.createFromEntity(entity = user.member, userId = userId, walletId = wallet.id)
    }
    @Transactional
    fun deleteUser(userId: Long) {
        val user = partnerMemberRepository.findByIdOrNull(userId) ?: throw DefaultException(errorCode = ErrorCode.USER_NOT_FOUND)
        val users = partnerMemberRepository.findByMember(user.member)
        user.status = UserStatusType.RETIRED

        val wallet = walletRepository.findByUser(user) ?: throw DefaultException(errorCode = ErrorCode.WALLET_NOT_FOUND)
        wallet.status = WalletStatusType.RETIRED
        if (users.count { it.status != UserStatusType.RETIRED } < 2) {
            // 오픈뱅킹 계정이 단일이라는 가정임
            val userSeqNum = users.firstOrNull { it.userSeqNum != null }?.userSeqNum
            userSeqNum?.let { openbankService.closeUser(it) }
        }
    }
}