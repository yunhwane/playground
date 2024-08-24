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

    /**
     *  쿼리문을 처리 한 후, 반환되는 값들을 그대로 리스트로 가져옵니다. 이떄, 반환되는 데이터가 없으면 빈 리스트(Empty List)가 반환
     */
    fun findAll(): List<User> {
        return jpaQueryFactory
            .selectFrom(user)
            .fetch()
    }




}