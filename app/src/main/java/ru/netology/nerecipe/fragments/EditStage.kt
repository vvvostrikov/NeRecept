package ru.netology.nerecipe.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecept.data.viewModel.RecipeViewModel
import ru.netology.nerecipe.R

import ru.netology.nerecipe.data.Stage
import ru.netology.nerecipe.databinding.FragmentEditStageBinding


class EditStage : Fragment() {


    private var idStage : Long = 0L
    private lateinit var stage : Stage
    private val viewModel : RecipeViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idStage = it.getLong(StageFragment.INITIAL_STAGE_KEY)

        }

    }

    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View {
        // Inflate the layout for this fragment
        //Get list stages from memory
        val listStages = viewModel.currentRecipe.value?.stages

        stage = if (idStage == 0L) {
            val maxPosition =
                if (listStages.isNullOrEmpty()) 0 else listStages.maxOf { it.position } + 1
            (listStages?.find { it.id == idStage } ?: Stage()).copy(
                    position = maxPosition ,
                    id = viewModel.nextIdStages()
            )
        } else {
            listStages?.find { it.id == idStage } ?: Stage()
        }

        val binding =
            FragmentEditStageBinding.inflate(layoutInflater , container , false).also { binding ->
                bind(binding)
            }

        val selectImageFromGalleryResult =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri : Uri? ->
                uri?.let {
                    requireActivity().contentResolver.takePersistableUriPermission(
                            uri ,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    val uridb = copyToDb(uri)
                    stage = stage.copy(photo = uridb.toString())
                    binding.stageImage.setImageURI(uridb)
                    binding.stageImage.visibility =
                        if (stage.photo.isBlank()) View.GONE else View.VISIBLE
                }

            }

        binding.setImage.setOnClickListener {

            selectImageFromGalleryResult.launch(arrayOf("image/*"))

        }

        binding.ok.setOnClickListener {
            val text = binding.textStageEdit.text

            if (! text.isNullOrBlank()) {
                stage = stage.copy(content = text.toString())
            } else {
                Toast.makeText(context , getString(R.string.stage_empty) , Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            val recipe = viewModel.currentRecipe.value

            val stages = if (idStage == 0L) (recipe?.stages
                ?: listOf()) + stage
            else recipe?.stages?.map { if (it.id == idStage) stage else it } ?: listOf()
            viewModel.currentRecipe.value = recipe?.copy(stages = stages)

            viewModel.dataStages.value = stages
            findNavController().popBackStack()

        }

        return binding.root
    }

    private fun copyToDb(uri : Uri) : Uri {
        return uri
    }

    private fun getCurrentPosition() : Int {
        val stages = viewModel.dataStages.value?.sortedBy { it.position }
        if (idStage == 0L) return (stages?.size ?: 0) + 1
        return (stages?.indexOfFirst { it.id == idStage } ?: 0) + 1
    }

    private fun bind(binding : FragmentEditStageBinding) = with(binding) {
        val stagePosition = getCurrentPosition()
        stageNumber.text = stagePosition.toString()
        textStageEdit.setText(stage.content)
        val imageUrl = stage.photo
        stageImage.setImageURI(Uri.parse(imageUrl))
        stageImage.visibility = if (stage.photo.isBlank()) View.GONE else View.VISIBLE

    }
}