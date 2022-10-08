package ru.netology.nerecipe.data

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
        val id: Long = 0 ,
        val describe: String = "" ,
        val author: String = "" ,
        val favorites: Boolean = false ,
        val stages: List<Stage> = listOf() ,
        val category: String = "" ,
        val photoRecipe: String = "" ,
        val indexOrder: Long = 0
)



