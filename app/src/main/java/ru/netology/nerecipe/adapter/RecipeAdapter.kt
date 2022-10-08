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
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeBinding


class RecipeAdapter(

    private val interactionListener : RecipeInteractionListener
) : ListAdapter<Recipe , RecipeAdapter.ViewHolder>(DiffCallback) {
    val differ : AsyncListDiffer<Recipe> = AsyncListDiffer(this , DiffCallback)

    class ViewHolder(private val binding : RecipeBinding , listener : RecipeInteractionListener) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var recipe : Recipe
        private val popupMenu by lazy {
            PopupMenu(itemView.context , binding.dropdownMenu).apply {
                inflate(R.menu.option_recipe)
                this.setOnMenuItemClickListener { menuItems ->
                    when (menuItems.itemId) {
                        R.id.remove -> {
                            listener.onDeleteClicked(recipe)
                            true
                        }
                        R.id.menu_edit -> {
                            listener.onEditClicked(recipe)
                            true
                        }
                        else -> false
                    }

                }
            }
        }

        init {
            binding.toggleButtonFavorit.setOnClickListener {
                listener.onLikeClicked(recipe)
            }

            binding.photo.setOnClickListener {
                listener.onNavigateClicked(recipe)
            }

            binding.describe.setOnClickListener {
                listener.onNavigateClicked(recipe)
            }
        }

        fun bind(recipe : Recipe) = with(binding) {
            this@ViewHolder.recipe = recipe
            describe.text = recipe.describe
            author.text = recipe.author
            val imageUrl = recipe.photoRecipe
            photo.setImageURI(Uri.parse(imageUrl))
            toggleButtonFavorit.isChecked = recipe.favorites
            category.text = recipe.category
            dropdownMenu.setOnClickListener { popupMenu.show() }
            videoGroup.visibility = if (recipe.photoRecipe.isBlank()) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeBinding.inflate(inflater , parent , false)
        return ViewHolder(binding , interactionListener)
    }

    override fun onBindViewHolder(holder : ViewHolder , position : Int) {
        holder.bind(getItem(position))

    }

    fun getRecipeId(position : Int) : Long {
        return getItem(position).id
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem : Recipe , newItem : Recipe) : Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem : Recipe , newItem : Recipe) : Boolean {
            return (oldItem.describe == newItem.describe &&
                    oldItem.stages == newItem.stages &&
                    oldItem.category == newItem.category &&
                    oldItem.favorites == newItem.favorites)
        }
    }
}