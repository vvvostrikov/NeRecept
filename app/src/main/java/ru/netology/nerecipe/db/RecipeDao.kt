package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nerecipe.data.Condition
import ru.netology.nerecipe.data.impl.RecipeRepository

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY indexOrder,id DESC")
    fun getAll() : LiveData<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id=:id")
    fun getRecipeById(id : Long) : RecipeEntity?

    @Insert
    fun insertRecipe(recipe : RecipeEntity) : Long

    @Query("UPDATE recipes SET indexOrder = id WHERE rowid = :rowId")
    fun updateRecipeIndexOrderAfterInsert(rowId : Long)

    fun insert(recipe : RecipeEntity) {
        val rowId = insertRecipe(recipe)
        updateRecipeIndexOrderAfterInsert(rowId)
    }

    @Query("UPDATE recipes SET describe = :describe, category=:category, photoRecipe = :photoRecipe, stages = :stages WHERE id = :id")
    fun updateRecipeById(
            id : Long ,
            describe : String ,
            photoRecipe : String ,
            stages : String ,
            category : Int
    )


    fun save(recipe : RecipeEntity) =
        if (recipe.id == RecipeRepository.NEW_RECIPE_ID) insert(recipe) else
            updateRecipeById(
                recipe.id ,
                recipe.describe ,
                recipe.photoRecipe ,
                recipe.stages ,
                recipe.category
            )

    @Query(
        """
        UPDATE recipes SET favorites =   CASE WHEN favorites = 0 THEN 1 ELSE 0 END
        WHERE id= :id
    """
    )
    fun toFavoriteById(id : Long)

    @Query("DELETE FROM recipes WHERE id=:id")
    fun removeById(id : Long)


    @Query(
        """
        UPDATE recipes SET indexOrder =   indexOrder+:direction
        WHERE id= :idItem
    """
    )
    fun reorderItems(direction : Int , idItem : Long)

    @Query("SELECT maxIdStages FROM condition ")
    fun getMaxIdofStages() : Long?

    @Insert
    fun insert(condition : ConditionEntity)

    @Query("UPDATE condition SET maxIdStages = :idStage WHERE id = 'default'")
    fun updateConditionDefault(idStage : Long)

    private fun updateIdStages(id : Long) =
        if (getMaxIdofStages() == null) insert(Condition().toEntity()) else
            updateConditionDefault(id)


    fun nextIdStages() : Long {
        val id = (getMaxIdofStages() ?: 0L) + 1L
        updateIdStages(id)
        return id
    }
}