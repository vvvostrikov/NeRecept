package ru.netology.nerecipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import ru.netology.nerecept.data.viewModel.RecipeViewModel
import ru.netology.nerecipe.databinding.ChipBinding
import ru.netology.nerecipe.databinding.FragmentCategoryFeedBinding


class CategoryFeed : Fragment() {

    private val viewModel : RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View =
        FragmentCategoryFeedBinding.inflate(layoutInflater , container , false).also { binding ->

            val nameList = FeedFragment.serviceRecipes.categoriesList
            var id = 0
            nameList.forEach {
                val chip = createChip(it)
                chip.id = id ++
                chip.isChecked = viewModel.filter.value?.categories?.contains(chip.id) ?: true
                binding.chipGroup.addView(chip)
            }

            binding.CategoryOk.setOnClickListener(onOkButtonClicked(binding))


        }.root

    private fun createChip(label : String) : Chip {
        val chip = ChipBinding.inflate(layoutInflater).root
        chip.text = label
        return chip
    }

    private fun onOkButtonClicked(binding : FragmentCategoryFeedBinding) : (View) -> Unit =
        {

            val checkedIds = binding.chipGroup.checkedChipIds
            val resultBundle = Bundle(1)
            resultBundle.putIntegerArrayList(
                    FeedFragment.RESULT_CATEGORY_KEY ,
                    checkedIds as ArrayList<Int>
            )
            setFragmentResult(requestKey = FeedFragment.REQUEST_CATEGORY_KEY , resultBundle)
            findNavController().popBackStack()
        }
}