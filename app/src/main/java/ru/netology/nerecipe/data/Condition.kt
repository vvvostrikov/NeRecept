package ru.netology.nerecipe.data

data class Condition(
    val id : String = "default" ,
    val maxIdStages : Long = 1L ,
    val filterCategory : String = ""
)

