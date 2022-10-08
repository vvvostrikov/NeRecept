package ru.netology.nerecipe.data

import kotlinx.serialization.Serializable

@Serializable
data class Stage(
    val id: Long = 0,
    val content: String = "",
    val position: Int = 1,
    val photo: String = ""
)