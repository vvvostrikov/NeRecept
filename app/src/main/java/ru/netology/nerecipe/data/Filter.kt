package ru.netology.nerecipe.data

data class Filter(
    val searchText: String,
    val categories: List<Int>
)