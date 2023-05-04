package com.tw2.prepaid.common.properties

import com.tw2.prepaid.common.EMPTY_MESSAGE

enum class SecretKey(val key: String, val defaultValue: String = EMPTY_MESSAGE) {
    DB_PASSWORD(key = "dbPassword"),
    DB_USERNAME(key = "dbUsername"),
    OB_PASS_PHRASE(key = "obPassPhrase"),
    OB_ACCESS_TOKEN(key = "obAccessToken"),
    OB_TRANSACTION_KEY(key = "obTransactionKey"),
    OB_CLIENT_ID(key = "obClientId"),
    OB_CLIENT_SECRET(key = "obClientSecret"),
    DB_ENCRYPTION_KEY(key = "dbEncryptionKey", defaultValue = "TRAVELWALLETTESTTRAVELWALLETTEST"),
    DB_ENCRYPTION_IV(key = "dbEncryptionIv", defaultValue = "TRAVELWALLETTEST"),
    API_ENCRYPTION_KEY(key = "apiEncryptionKey", defaultValue = "TRAVELWALLETTESTTRAVELWALLETTEST"),
    API_ENCRYPTION_IV(key = "apiEncryptionIv", defaultValue = "TRAVELWALLETTEST"),
}