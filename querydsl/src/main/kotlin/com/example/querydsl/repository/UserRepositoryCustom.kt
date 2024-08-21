package com.example.querydsl.repository

import com.example.querydsl.entity.QUser.user
import com.example.querydsl.entity.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryCustom(
    private val jpaQueryFactory: JPAQueryFactory
) {
    fun findUserByEmail(email: String): User? {
        return jpaQueryFactory
            .selectFrom(user)
            .where(user.email.eq(email))
            .fetchOne()
    }
}