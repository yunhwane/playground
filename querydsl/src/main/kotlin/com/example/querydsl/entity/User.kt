package com.example.querydsl.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "users")
class User(
    val email: String,
    val password: String,
    val name: String,
    val age: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null


}