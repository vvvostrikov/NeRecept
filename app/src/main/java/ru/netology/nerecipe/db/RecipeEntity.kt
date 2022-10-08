package ru.netology.nerecipe.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recipes")
class RecipeEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id : Long = - 1L ,
        val describe : String = "" ,
        val author : String = "" ,
        @ColumnInfo(name = "favorites")
        val favorites : Boolean = false ,
        val stages : String = "" ,
        val photoRecipe : String = "" ,
        val category : Int = 0 ,
        val indexOrder : Long = 0L
)

@Entity(tableName = "condition")
class ConditionEntity(
        @PrimaryKey
        val id : String ,
        val maxIdStages : Long ,
        val filterCategory : String
)

