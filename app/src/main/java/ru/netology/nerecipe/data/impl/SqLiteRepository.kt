package ru.netology.nerecipe.data.impl


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.data.Stage
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toModel
import ru.netology.nerecipe.utils.SingleLiveEvent

class SqLiteRepository(private val dao : RecipeDao) : RecipeRepository {

    override val data = dao.getAll().map { entities ->
        entities.map { it.toModel() }
    }

    override val sharePostContent = SingleLiveEvent<String>()
    override val currentRecipe = MutableLiveData<Recipe?>(null)
    override val currentStage = MutableLiveData<Stage?>(null)

    override val stages = MutableLiveData<List<Stage>>(null)

    override fun like(id : Long) {
        dao.toFavoriteById(id)
    }


    override fun delete(id : Long) {
        dao.removeById(id)
    }


    override fun save(recipe : Recipe) {
        dao.save(recipe.toEntity())
    }


    override fun getRecipeById(id : Long) : Recipe? {
        return dao.getRecipeById(id)?.toModel()
    }

    override fun onMoveItem(to : Int , from : Int , recipeToId : Long , recipeFromId : Long) {
        if (to == from) return

        dao.reorderItems(if (to < from) -1 else 1 , recipeFromId)
        dao.reorderItems(if (to < from) 1 else -1 , recipeToId)
    }

    override fun nextIdStages() : Long {
        return dao.nextIdStages()
    }
}