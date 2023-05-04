package com.tw2.prepaid.common.jpa

import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.data.jpa.repository.support.Querydsl
import org.springframework.data.querydsl.SimpleEntityPathResolver
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
abstract class Querydsl4RepositorySupport(private val domainClass: Class<*>) {
    private lateinit var querydsl: Querydsl
    protected lateinit var em: EntityManager
    @Autowired
    lateinit var jpaQueryFactory: JPAQueryFactory

    @Autowired
    fun setEntityManager(em: EntityManager) {
        val entityInformation =
                JpaEntityInformationSupport.getEntityInformation(domainClass, em)
        val resolver: SimpleEntityPathResolver = SimpleEntityPathResolver.INSTANCE
        val path = resolver.createPath(entityInformation.javaType)
        this.em = em
        querydsl = Querydsl(em, PathBuilder(path.type, path.metadata))
    }

    protected fun <T> applyPagination(
            pageable: Pageable,
            contentQuery: Function1<JPAQueryFactory, JPAQuery<T>>
    ): Page<T> {
        val jpaQuery: JPAQuery<T> = contentQuery.invoke(jpaQueryFactory)
        val content: List<T> = querydsl.applyPagination(pageable, jpaQuery).fetch()
        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount)
    }
}