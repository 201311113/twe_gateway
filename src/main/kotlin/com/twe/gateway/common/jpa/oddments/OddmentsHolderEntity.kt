package com.tw2.prepaid.common.jpa.oddments

import com.tw2.prepaid.common.jpa.BaseEntity
import com.tw2.prepaid.common.jpa.EncryptConverter
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "oddments_holder")
class OddmentsHolderEntity(
    @Convert(converter = EncryptConverter::class)
    var oddment: String,
    @Enumerated(value = EnumType.STRING)
    val category: OddmentsCategory,
): BaseEntity()