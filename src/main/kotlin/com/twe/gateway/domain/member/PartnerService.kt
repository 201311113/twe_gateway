package com.tw2.prepaid.domain.member

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.domain.member.dto.request.PartnerRequest
import com.tw2.prepaid.domain.member.dto.response.PartnerResponse
import com.tw2.prepaid.domain.member.model.repository.PartnerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartnerService(
    private val partnerRepository: PartnerRepository
) {
    fun createPartner(req: PartnerRequest) =
        PartnerResponse.createFromEntity(partnerRepository.save(req.toEntity()))
    fun getPartners(pageable: Pageable): Page<PartnerResponse> =
        partnerRepository.findAll(pageable).map(PartnerResponse::createFromEntity)
    fun getPartner(partnerId: Long) = PartnerResponse.createFromEntity(getPartnerInner(partnerId))
    @Transactional
    fun updatePartner(partnerId: Long, req: PartnerRequest): PartnerResponse {
        val partner = getPartnerInner(partnerId)
        req.updateEntity(partner)
        return PartnerResponse.createFromEntity(partner)
    }
    fun deletePartner(partnerId: Long) = partnerRepository.deleteById(partnerId)
    private fun getPartnerInner(partnerId: Long) = partnerRepository.findByIdOrNull(partnerId)
        ?: throw DefaultException(message = "partner is not found.")
}