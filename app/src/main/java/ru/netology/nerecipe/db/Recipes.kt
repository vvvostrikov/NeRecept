package ru.netology.nerecipe.db

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.netology.nerecipe.data.Condition
import ru.netology.nerecipe.fragments.FeedFragment
import ru.netology.nerecipe.data.Recipe


fun RecipeEntity.toModel() = Recipe(
        id = id ,
        describe = describe ,
        author = author ,
        photoRecipe = photoRecipe ,
        stages = if (stages.isBlank()) listOf() else Json.decodeFromString(stages) ,
        favorites = favorites ,
        category = FeedFragment.serviceRecipes.getCategoryById(category) ,
        indexOrder = indexOrder
)

fun Recipe.toEntity() = RecipeEntity(
        id = id ,
        describe = describe ,
        author = author ,
        photoRecipe = photoRecipe ,
        stages = Json.encodeToString(stages) ,
        favorites = favorites ,
        category = FeedFragment.serviceRecipes.getIdbyCategory(category) ,
        indexOrder = indexOrder
)

fun Condition.toEntity() = ConditionEntity(
        id = id ,
        maxIdStages = maxIdStages ,
        filterCategory = filterCategory
)

fun ConditionEntity.toModel() = Condition(
        id = id ,
        maxIdStages = maxIdStages ,
        filterCategory = filterCategory
)