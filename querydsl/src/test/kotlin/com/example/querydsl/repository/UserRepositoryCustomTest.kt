package com.example.querydsl.repository

import com.example.querydsl.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import


@SpringBootTest
class UserRepositoryCustomTest(
    @Autowired private val userRepositoryCustom: UserRepositoryCustom
){


    @Test
    @DisplayName("유저 이메일로 유저를 조회한다.")
    fun `should user when email`() {
        // given
        val email = "john.doe@example.com"

        // when
        val user:User? = userRepositoryCustom.findUserByEmail(email)

        // then
        assertThat(user?.email).isEqualTo(email)
        assertThat(user?.id).isEqualTo(1)
    }
}