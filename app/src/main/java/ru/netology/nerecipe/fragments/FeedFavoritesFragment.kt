package ru.netology.nerecipe.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecept.data.viewModel.RecipeViewModel
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.Filter

import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.data.impl.RecipeRepository
import ru.netology.nerecipe.databinding.FragmentFavoritesFeedBinding


class FeedFavoritesFragment : Fragment() {

    val viewModel : RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private lateinit var adapter : RecipeAdapter

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END , 0) {

                override fun onMove(
                    recyclerView : RecyclerView ,
                    viewHolder : RecyclerView.ViewHolder ,
                    target : RecyclerView.ViewHolder
                ) : Boolean {
                    val adapter = recyclerView.adapter as RecipeAdapter
                    val from = viewHolder.absoluteAdapterPosition
                    val to = target.absoluteAdapterPosition

                    adapter.notifyItemMoved(from , to)
                    viewModel.onMoveItem(
                            from ,
                            to ,
                            adapter.getRecipeId(from) ,
                            adapter.getRecipeId(to)
                    )
                    return true
                }

                override fun onSwiped(viewHolder : RecyclerView.ViewHolder , direction : Int) {
                }

                override fun onSelectedChanged(
                    viewHolder : RecyclerView.ViewHolder? ,
                    actionState : Int
                ) {
                    super.onSelectedChanged(viewHolder , actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView : RecyclerView ,
                    viewHolder : RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView , viewHolder)

                    viewHolder.itemView.alpha = 1.0f
                }
            }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreateView(
        inflater : LayoutInflater ,
        container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View =
        FragmentFavoritesFeedBinding.inflate(layoutInflater , container , false).also { binding ->

            adapter = RecipeAdapter(viewModel)
            itemTouchHelper.attachToRecyclerView(binding.container)
            binding.container.adapter = adapter

            viewModel.dataViewModel.observe(viewLifecycleOwner) {
                val filteredResult = viewModel.getFilteredResult().filter { it.favorites }
                adapter.submitList(filteredResult)
                adapter.differ.submitList(filteredResult)
                binding.emptyPic.visibility =
                    if (filteredResult.isEmpty()) View.VISIBLE else View.GONE
            }


            setFragmentResultListener(requestKey = FeedFragment.REQUEST_CATEGORY_KEY) { requestKey , bundle ->
                if (requestKey != FeedFragment.REQUEST_CATEGORY_KEY) return@setFragmentResultListener
                val categories =
                    bundle.getIntegerArrayList(FeedFragment.RESULT_CATEGORY_KEY)
                        ?: return@setFragmentResultListener

                val filter =
                    viewModel.filter.value?.copy(categories = categories) ?: Filter(
                            "" ,
                            categories
                    )
                viewModel.onChangeFilters(filter)
            }

        }.root

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        // The usage of an interface lets you inject your own implementation
        val menuHost : MenuHost = requireActivity()
        view.background.alpha = 50
        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu : Menu , menuInflater : MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_filter , menu)
                val searchMenuItem = menu.findItem(R.id.action_search_text)
                val searchView = searchMenuItem.actionView as SearchView
                val textView = resources.getString(R.string.search_text)
                searchView.queryHint = textView
                val categoriesList = resources.getStringArray(R.array.categories).toList()
                searchView.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(searchText : String?) : Boolean {
                        searchView.clearFocus()

                        val filter = viewModel.filter.value?.copy(searchText = searchText ?: "")
                            ?: Filter(
                                    searchText ?: "" ,
                                    List(categoriesList.size) { index -> index })

                        viewModel.onChangeFilters(filter)
                        return false
                    }

                    override fun onQueryTextChange(searchText : String?) : Boolean {

                        val filter = viewModel.filter.value?.copy(searchText = searchText ?: "")
                            ?: Filter(
                                    searchText ?: "" ,
                                    List(categoriesList.size) { index -> index })

                        viewModel.onChangeFilters(filter)
                        return true
                    }

                })
            }


            override fun onMenuItemSelected(menuItem : MenuItem) : Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_search_text -> {

                        true
                    }
                    R.id.action_search_category -> {
                        findNavController().navigate(
                                R.id.action_feedFavoritesFragment_to_categoryFeed ,

                                )
                        true
                    }
                    else -> false
                }
            }
        } , viewLifecycleOwner , Lifecycle.State.RESUMED)

    }


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val categoriesList = resources.getStringArray(R.array.categories).toList()
        viewModel.filter.value = Filter("" , List(categoriesList.size) { index -> index })

        viewModel.filter.observe(this) {
            val filteredResult = viewModel.getFilteredResult()
            adapter.submitList(filteredResult)
        }

        viewModel.navigateToRecipeSingle.observe(this) { recipeToSingle ->
            viewModel.currentRecipe.value = recipeToSingle
            viewModel.dataStages.value = recipeToSingle.stages
            findNavController().navigate(
                    R.id.singleRecipe ,
                    SingleRecipeFragment.createBundle(recipeToSingle.id)
            )
        }
        viewModel.navigateToRecipeScreenEvent.observe(this) { recipeToEdit ->
            findNavController().navigate(
                    R.id.action_feedFavoritesFragment_to_editRecipe ,
                    EditRecipe.createBundle(recipeToEdit?.id ?: RecipeRepository.NEW_RECIPE_ID)
            )
        }
    }
}