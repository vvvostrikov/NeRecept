package ru.netology.nerecipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.nerecipe.R

class StageFragment : Fragment() {

    override fun onCreateView(
            inflater : LayoutInflater , container : ViewGroup? ,
            savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stage , container , false)
    }

    companion object {

        const val INITIAL_STAGE_KEY = "Initial stage key"
        fun createBundle(idRecipe : Long?) =
            Bundle(1).apply {

                putLong(INITIAL_STAGE_KEY , idRecipe ?: 0L)
            }
    }
}