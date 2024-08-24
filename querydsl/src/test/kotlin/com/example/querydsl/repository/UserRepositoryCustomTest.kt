package com.example.querydsl.repository

import com.example.querydsl.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
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

    @Test
    @DisplayName("모든 유저를 조회한다.")
    fun `should all user`() {
        // given when
        val users: List<User> = userRepositoryCustom.findAll();

        assertThat(users).extracting(
            User::id,
            User::email,
            User::name,
            User::age
        ).containsExactly(
            tuple(1L, "john.doe@example.com", "John Doe", 25,1),
            tuple(2L, "jane.doe@example.com", "Jane Doe", 30),
            tuple(3L, "alice.smith@example.com", "Alice Smith", 22),
            tuple(4L, "bob.johnson@example.com", "Bob Johnson", 28)
        );
    }

    @Test
    @DisplayName("팀 이름으로 유저를 조회한다.")  // "Fetch users by team name."
    fun `should find users by team name`() {
        // given
        val teamName = "Engineering"

        // when
        val users: List<User> = userRepositoryCustom.findUserByTeamNameJoinFetch(teamName)

        // then
        assertThat(users).extracting(
            User::id,
            User::email,
            User::name,
            User::age
        ).containsExactly(
            tuple(1L, "john.doe@example.com", "John Doe", 25),
            tuple(3L, "alice.smith@example.com", "Alice Smith", 22)
        )
    }
}