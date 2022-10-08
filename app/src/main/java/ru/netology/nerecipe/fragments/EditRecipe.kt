package ru.netology.nerecipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

import ru.netology.nerecipe.adapter.StageAdapter
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecept.data.viewModel.RecipeViewModel
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentEditRecipeBinding

class EditRecipe : Fragment() {
    val viewModel : RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private var idRecipe : Long = 0
    private lateinit var recipe : Recipe
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END ,
                    0
            ) {

                override fun onMove(
                    recyclerView : RecyclerView ,
                    viewHolder : RecyclerView.ViewHolder ,
                    target : RecyclerView.ViewHolder
                ) : Boolean {
                    val adapter = recyclerView.adapter as StageAdapter
                    val from = viewHolder.absoluteAdapterPosition
                    val to = target.absoluteAdapterPosition

                    viewHolder.itemView.findViewById<TextView>(R.id.stage_number).text =
                        (to + 1).toString()
                    target.itemView.findViewById<TextView>(R.id.stage_number).text =
                        (from + 1).toString()

                    adapter.notifyItemMoved(from , to)
                    viewModel.onMoveItemStage(
                            from ,
                            to ,
                            adapter.getStagebyPosition(from) ,
                            adapter.getStagebyPosition(to)
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

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
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

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idRecipe = it.getLong(INITIAL_RECIPE_KEY)

        }

        viewModel.navigateToStageScreenEvent.observe(this) { stage ->
            findNavController().navigate(
                    R.id.action_editRecipe_to_editStage ,
                    StageFragment.createBundle(stage?.id ?: 0L)
            )
        }
    }

    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View =
        FragmentEditRecipeBinding.inflate(layoutInflater , container , false).also { binding ->

            //после редактирования шагов(без записи) и перерисовки должен восстанавливать
            // данные из памяти а не из БД:
            recipe = viewModel.currentRecipe.value ?: Recipe()

            bind(binding)

            itemTouchHelper.attachToRecyclerView(binding.container)
            binding.fabStage.setOnClickListener {
                viewModel.currentRecipe.value = viewModel.currentRecipe.value ?: Recipe()
                viewModel.onAddStageClicked()
            }

            binding.ok.setOnClickListener {
                val text = binding.recipeDescribe.text
                val category = binding.spinner.selectedItem.toString()
                val stages = viewModel.currentRecipe.value?.stages ?: listOf()

                if (! text.isNullOrBlank() && stages.isNotEmpty()) {
                    recipe =
                        recipe.copy(
                                category = category ,
                                stages = stages ,
                                describe = text.toString()
                        )
                } else {
                    Toast.makeText(context , getString(R.string.emtyDescribe) , Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                viewModel.currentRecipe.value = recipe
                viewModel.onSaveButtonClicked()
                viewModel.currentRecipe.value = null
                findNavController().popBackStack()
            }

            setAdapter(binding)

            viewModel.dataStages.value = recipe.stages

        }.root

    private fun setAdapter(binding : FragmentEditRecipeBinding) {

        val adapter = StageAdapter(viewModel , true)

        binding.container.adapter = adapter

        viewModel.dataStages.observe(viewLifecycleOwner) { stages ->
            val list = stages.sortedBy { it.position }
            adapter.submitList(list)
            adapter.differ.submitList(list)
            binding.emptyPovar.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

        }
        viewModel.DeleteStageSingle.observe(viewLifecycleOwner) {
            setAdapter(binding)
        }


    }

    private fun bind(binding : FragmentEditRecipeBinding) = with(binding) {


        recipeDescribe.setText(recipe.describe)
        val positionCategory = resources.getStringArray(R.array.categories).indexOf(recipe.category)

        ArrayAdapter.createFromResource(
                requireContext() ,
                R.array.categories ,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }


        spinner.setSelection(positionCategory)
        binding.root.bringToFront()
    }

    companion object {

        const val INITIAL_RECIPE_KEY = "openEditedRecipe"
        const val REQUEST_KEY_CHAHGE = "request key change"

        fun createBundle(RecipeId : Long) = Bundle(1).apply {
            putLong(INITIAL_RECIPE_KEY , RecipeId)
        }
    }
}