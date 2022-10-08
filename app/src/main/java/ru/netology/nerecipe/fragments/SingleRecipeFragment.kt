package ru.netology.nerecipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import ru.netology.nerecipe.adapter.StageAdapter
import ru.netology.nerecipe.data.impl.RecipeRepository
import ru.netology.nerecept.data.viewModel.RecipeViewModel
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentSingleRecipeBinding


class SingleRecipeFragment : Fragment() {

    private var idRecipe : Long? = null
    private val viewModel : RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idRecipe = it.getLong(INITIAL_RECIPE_KEY)
        }
    }

    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View {

        val binding =
            FragmentSingleRecipeBinding.inflate(layoutInflater , container , false).also { binding->
                bind(binding)
            }

        val adapter = StageAdapter(viewModel , false)

        binding.container.adapter = adapter

        //observe

        viewModel.dataStages.observe(viewLifecycleOwner) { stages->
            val list = stages.sortedBy { it.position }
            adapter.submitList(list)
            binding.emptyPovar.visibility =
                if (stages?.isEmpty() != false) View.VISIBLE else View.GONE

        }

        viewModel.dataViewModel.observe(viewLifecycleOwner) {
            bind(binding)
        }

        viewModel.navigateToRecipeScreenEvent.observe(viewLifecycleOwner) { recipeToEdit->
            findNavController().navigate(
                    R.id.action_singleRecipe_to_editRecipe ,
                    EditRecipe.createBundle(recipeToEdit?.id ?: RecipeRepository.NEW_RECIPE_ID)
            )
        }

        setFragmentResultListener(requestKey = EditRecipe.REQUEST_KEY_CHAHGE) { requestKey , _->
            if (requestKey != EditRecipe.REQUEST_KEY_CHAHGE) return@setFragmentResultListener
            // viewModel.onSaveButtonClicked()
        }

        return binding.root
    }

    private fun bind(binding : FragmentSingleRecipeBinding) = with(binding) {

        val recipe = viewModel.getRecipeByIdFromLiveData(idRecipe) ?: return@with

        toggleButtonFavorit.isChecked = recipe.favorites
        describe.text = recipe.describe
        author.text = recipe.author
        category.text = recipe.category
        viewModel.dataStages.value = recipe.stages

        val popupMenu by lazy {
            PopupMenu(requireContext() , binding.dropdownMenu).apply {
                inflate(R.menu.option_recipe)
                this.setOnMenuItemClickListener { menuItems->
                    when (menuItems.itemId) {
                        R.id.remove -> {
                            viewModel.onDeleteClicked(recipe = recipe)
                            findNavController().popBackStack()
                            true
                        }
                        R.id.menu_edit -> {
                            viewModel.onEditClicked(recipe)
                            true
                        }
                        else -> false
                    }

                }
            }
        }

        binding.dropdownMenu.setOnClickListener { popupMenu.show() }

        binding.toggleButtonFavorit.setOnClickListener {
            viewModel.onLikeClicked(recipe)

        }
    }

    companion object {

        const val INITIAL_RECIPE_KEY = "openSingleRecipe"

        fun createBundle(idRecipe : Long?) =
            Bundle(1).apply {
                putLong(INITIAL_RECIPE_KEY , idRecipe ?: 0)
            }
    }
}