package com.tw2.prepaid.domain.bank.service

import com.tw2.prepaid.common.error.DefaultException
import com.tw2.prepaid.common.error.ErrorCode
import com.tw2.prepaid.common.jpa.oddments.OddmentsCategory
import com.tw2.prepaid.common.jpa.oddments.OddmentsHolderEntityRepository
import com.tw2.prepaid.domain.bank.dto.response.ObAccessToken
import org.springframework.stereotype.Service

@Service
class OpenbankTokenService(
    private val oddmentsHolderEntityRepository: OddmentsHolderEntityRepository,
) {
    fun getAccessToken(): ObAccessToken {
        val entity = oddmentsHolderEntityRepository.findByCategory(OddmentsCategory.OB_ACCESS_TOKEN)
            ?: throw DefaultException(errorCode = ErrorCode.DATA_NOT_FOUND)
        return ObAccessToken(
            updatedAt = entity.updatedAt,
            accessToken = entity.oddment
        )
    }
}