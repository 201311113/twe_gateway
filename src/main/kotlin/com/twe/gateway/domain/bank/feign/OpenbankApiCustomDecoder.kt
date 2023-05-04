package com.tw2.prepaid.domain.bank.feign

import com.fasterxml.jackson.databind.node.ObjectNode
import com.tw2.prepaid.common.EMPTY_MESSAGE
import feign.Response
import feign.Util
import feign.codec.Decoder
import mu.KotlinLogging
import org.apache.commons.io.output.StringBuilderWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

private val log = KotlinLogging.logger {}
const val OPENBANK_RES_CODE_FIELD = "rsp_code"
const val OPENBANK_RES_MSG_FIELD = "rsp_message"
const val OPENBANK_BANK_RES_CODE_FIELD = "bank_rsp_code"
const val OPENBANK_RES_LIST_FIELD = "res_list"

val RETRY_PROCESSING_PATHS = listOf(WITHDRAW_PATH, DEPOSIT_PATH)

class OpenbankApiCustomDecoder(private val delegate: Decoder): Decoder {
    override fun decode(response: Response, type: Type): Any {
        val writer = StringBuilderWriter()
        response.body().asReader(Charset.defaultCharset()).transferTo(writer)
        val responseBody = writer.toString()

        // response body parsing.
        //val jsonNode = mapper.readValue(responseBody, ObjectNode::class.java)
        //val rspCode = jsonNode[OPENBANK_RES_CODE_FIELD].asText()
        //val rspMsg = jsonNode[OPENBANK_RES_MSG_FIELD].asText()

        // TODO 에러 핸들링은 그냥 OpenbankService 로 빼야할듯
        // handleOpenbankResponse(code = rspCode, message = rspMsg)

        //if (RETRY_PROCESSING_PATHS.contains(response.request().requestTemplate().path())) {
            //val bankRspCode = extractBankRspCode(jsonNode)
            // TODO("재처리 로직")
        //}

        return delegate.decode(response.toBuilder().body(responseBody, Util.UTF_8).build(), type)
    }

    private fun extractBankRspCode(jsonNode: ObjectNode): String {
        val rspCode1 = jsonNode.get(OPENBANK_BANK_RES_CODE_FIELD)?.asText() ?: EMPTY_MESSAGE
        val rspCode2 = jsonNode.get(OPENBANK_RES_LIST_FIELD)
            ?.get(0)?.get(OPENBANK_BANK_RES_CODE_FIELD)?.asText() ?: EMPTY_MESSAGE

        return rspCode1 + rspCode2
    }
}