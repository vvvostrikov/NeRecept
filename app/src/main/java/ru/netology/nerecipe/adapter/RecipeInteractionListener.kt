package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.data.Filter
import ru.netology.nerecipe.data.Recipe

interface RecipeInteractionListener {
    fun onLikeClicked(recipe: Recipe)
    fun onDeleteClicked(recipe: Recipe)
    fun onEditClicked(recipe: Recipe)
    fun onNavigateClicked(recipe: Recipe)
    fun onChangeFilters(filter: Filter?)
}