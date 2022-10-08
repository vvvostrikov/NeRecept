package ru.netology.nerecept.data.viewModel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.netology.nerecipe.data.Filter
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.data.Stage
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.adapter.StageInteractionListener
import ru.netology.nerecipe.data.impl.RecipeRepository
import ru.netology.nerecipe.data.impl.SqLiteRepository
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.utils.SingleLiveEvent

class RecipeViewModel(application : Application) : AndroidViewModel(application) ,
    RecipeInteractionListener , StageInteractionListener {

    private val repository : RecipeRepository = SqLiteRepository(
        dao = AppDb.getInstance(
            context = application
        ).recipeDao
    )
    val dataViewModel by repository::data  //all recipes
    val dataStages by repository::stages    // current recipe stages

    val navigateToRecipeScreenEvent = SingleLiveEvent<Recipe?>() //editing recipe
    val navigateToStageScreenEvent = SingleLiveEvent<Stage?>() //editing stage
    val navigateToRecipeSingle = SingleLiveEvent<Recipe>() // open single recipe
    val DeleteStageSingle = SingleLiveEvent<Recipe?>()
    val filter = SingleLiveEvent<Filter>()  //change filters
    var listAllCategories = listOf<String>()
    val currentRecipe by repository::currentRecipe //current recipe

    override fun onLikeClicked(recipe : Recipe) = repository.like(recipe.id)
    override fun onDeleteClicked(recipe : Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe : Recipe) {
        currentRecipe.value = recipe
        navigateToRecipeScreenEvent.value = recipe
    }

    override fun onNavigateClicked(recipe : Recipe) {
        navigateToRecipeSingle.value = recipe
    }

    override fun onChangeFilters(filter : Filter?) {
        this.filter.value = filter
    }


    fun getRecipeByIdFromLiveData(id : Long?) : Recipe? = dataViewModel.value?.find { it.id == id }

    fun onSaveButtonClicked() {

        val editedRecipe =
            currentRecipe.value?.copy(author = getUserName()) ?: Recipe(
                id = RecipeRepository.NEW_RECIPE_ID ,

                author = getUserName() ,

                )
        currentRecipe.value = editedRecipe
        repository.save(editedRecipe)
    }

    private fun getUserName() : String {
        return "Рецепт от Дяди Вани"
    }

    fun getFilteredResult() : List<Recipe> {
        val filtered : MutableList<Recipe> = mutableListOf()
        val recipes = dataViewModel.value ?: return filtered
        val searchText = filter.value?.searchText
        val searchCategory = filter.value?.categories
        if (! (searchText == null || searchText.isBlank())) {
            filtered.addAll(recipes.filter {
                it.describe.lowercase().contains(searchText.lowercase())
                        && searchCategory?.contains(listAllCategories.indexOf(it.category)) ?: true

            })
        } else {
            if (searchCategory != null)
                filtered.addAll(recipes.filter {
                    searchCategory.contains(
                        listAllCategories.indexOf(
                            it.category
                        )
                    )
                })
            else return recipes
        }
        return filtered
    }

    fun onAddClicked() {
        currentRecipe.value = null
        navigateToRecipeScreenEvent.value = null
    }

    fun onAddStageClicked() {
        navigateToStageScreenEvent.value = null
    }

    fun nextIdStages() : Long {
        return repository.nextIdStages()
    }

    fun onMoveItem(to : Int , from : Int , recipeToId : Long , recipeFromId : Long) {
        repository.onMoveItem(to , from , recipeToId , recipeFromId)
    }

    fun onMoveItemStage(to : Int , from : Int , stageTo : Stage , stageFrom : Stage) {
        val list = currentRecipe.value?.stages ?: return
        val fromStage = list.find { it.id == stageFrom.id }
        val toStage = list.find { it.id == stageTo.id }

        val fromLocation =
            fromStage?.copy(position = fromStage.position + if (to < from) - 1 else 1)
        val toLocation = toStage?.copy(position = toStage.position + if (to < from) 1 else - 1)

        val res = list.map {
            when (it.id) {
                fromLocation?.id -> fromLocation
                toLocation?.id -> toLocation
                else -> it
            }
        }
        val recipe = currentRecipe.value?.copy(stages = res)
        currentRecipe.value = recipe
        dataStages.value = res
    }

    override fun onDeleteClicked(stage : Stage) {
        val stages = currentRecipe.value?.stages?.toMutableList()
        stages?.removeAll { it.id == stage.id }
        val recipe = currentRecipe.value?.copy(stages = stages ?: listOf())
        currentRecipe.value = recipe
        dataStages.value = stages
        DeleteStageSingle.call()
    }

    override fun onEditClicked(stage : Stage) {
        navigateToStageScreenEvent.value = stage
    }
}