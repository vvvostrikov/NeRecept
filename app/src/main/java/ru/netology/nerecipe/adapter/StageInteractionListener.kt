package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.data.Stage

interface StageInteractionListener {

    fun onDeleteClicked(stage: Stage)
    fun onEditClicked(stage: Stage)
}