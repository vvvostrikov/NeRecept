package ru.netology.nerecipe.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.data.Stage
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.StageBinding


class StageAdapter(

        private val interactionListener : StageInteractionListener ,
        private val isEdit : Boolean = false ,

        ) : ListAdapter<Stage , StageAdapter.ViewHolder>(DiffCallback) {
    val differ : AsyncListDiffer<Stage> = AsyncListDiffer(this , DiffCallback)

    class ViewHolder(private val binding : StageBinding , listener : StageInteractionListener) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var stage : Stage
        private val popupMenu by lazy {
            PopupMenu(itemView.context , binding.dropdownMenu).apply {
                inflate(R.menu.option_recipe)
                this.setOnMenuItemClickListener { menuItems ->
                    when (menuItems.itemId) {
                        R.id.remove -> {
                            listener.onDeleteClicked(stage)
                            true
                        }
                        R.id.menu_edit -> {
                            listener.onEditClicked(stage)
                            true
                        }
                        else -> false
                    }
                }
            }
        }

        fun bind(stage : Stage) = with(binding) {
            this@ViewHolder.stage = stage
            textStage.text = stage.content
            val imageUrl = stage.photo
            photo.setImageURI(Uri.parse(imageUrl))
            val pos = absoluteAdapterPosition + 1
            stageNumber.text = pos.toString()
            dropdownMenu.setOnClickListener { popupMenu.show() }
            photo.visibility = if (stage.photo.isBlank()) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StageBinding.inflate(inflater , parent , false)
        binding.dropdownMenu.visibility = if (isEdit) View.VISIBLE else View.GONE
        return ViewHolder(binding , interactionListener)
    }

    override fun onBindViewHolder(holder : ViewHolder , position : Int) {
        val stage = currentList[position]
        holder.bind(stage)
    }

    fun getStagebyPosition(position : Int) : Stage {
        return getItem(position)
    }

    private object DiffCallback : DiffUtil.ItemCallback<Stage>() {
        override fun areItemsTheSame(oldItem : Stage , newItem : Stage) : Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem : Stage , newItem : Stage) : Boolean {
            return oldItem.content == newItem.content &&
                    oldItem.photo == newItem.photo
        }
    }
}