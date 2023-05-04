package com.tw2.prepaid.common.configuration

import com.tw2.prepaid.common.properties.PrepaidProperties
import com.tw2.prepaid.common.properties.SecretKey
import com.tw2.prepaid.common.utils.getRequestAttribute
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.StringUtils
import javax.sql.DataSource

const val PRIMARY_DB = "PRIMARY_DB"
const val SECONDARY_DB = "SECONDARY_DB"

@Configuration
class DataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    fun primaryDataSourceProperties() = DataSourceProperties()

    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    fun secondaryDataSourceProperties() = DataSourceProperties()

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary.hikari")
    fun primaryDataSource(primaryDataSourceProperties: DataSourceProperties, pp: PrepaidProperties) =
        hikariDataSource(primaryDataSourceProperties, pp)

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.secondary.hikari")
    fun secondaryDataSource(secondaryDataSourceProperties: DataSourceProperties, pp: PrepaidProperties) =
        hikariDataSource(secondaryDataSourceProperties, pp)

    @Bean
    @Primary
    fun auroraDataSource(properties: PrepaidProperties,
                         primaryDataSource: DataSource,
                         secondaryDataSource: DataSource): DataSource {
        val ds = object : AbstractRoutingDataSource() {
            override fun determineCurrentLookupKey() =
                if (properties.alwaysUsePrimaryDb || getRequestAttribute(PRIMARY_DB) != null)
                    PRIMARY_DB
                else if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                    || !TransactionSynchronizationManager.isSynchronizationActive()
                ) SECONDARY_DB
                else PRIMARY_DB
        }
        ds.setDefaultTargetDataSource(primaryDataSource)
        ds.setTargetDataSources(mapOf(PRIMARY_DB to primaryDataSource, SECONDARY_DB to secondaryDataSource))
        ds.afterPropertiesSet()
        return LazyConnectionDataSourceProxy(ds)
    }
    private fun hikariDataSource(dbProperties: DataSourceProperties, pp: PrepaidProperties): HikariDataSource {
        dbProperties.password = pp.getSecretValue(SecretKey.DB_PASSWORD)
        dbProperties.username = pp.getSecretValue(SecretKey.DB_USERNAME)
        val dataSource = createDataSource<HikariDataSource>(
            dbProperties,
            HikariDataSource::class.java
        )
        if (StringUtils.hasText(dbProperties.name)) {
            dataSource.poolName = dbProperties.name
        }
        return dataSource
    }
}

fun <T> createDataSource(properties: DataSourceProperties, type: Class<out DataSource>): T {
    return properties.initializeDataSourceBuilder().type(type).build() as T
}