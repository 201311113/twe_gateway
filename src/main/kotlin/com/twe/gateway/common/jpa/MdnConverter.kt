package com.tw2.prepaid.common.jpa

import javax.persistence.AttributeConverter

const val NO_MASK_DIGIT = 5
const val MASKING_STR = "*"

class MdnConverter: AttributeConverter<String, String> {
    override fun convertToDatabaseColumn(attribute: String?): String? =
        attribute?.replaceRange(0,attribute.length - NO_MASK_DIGIT, MASKING_STR)

    override fun convertToEntityAttribute(dbData: String?): String? = dbData
}