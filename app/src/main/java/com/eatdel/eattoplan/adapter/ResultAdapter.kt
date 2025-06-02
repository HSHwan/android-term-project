package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.SavedResult
import com.eatdel.eattoplan.databinding.ItemResultBinding

class ResultAdapter(
    private val items: List<SavedResult>,
    private val onItemClick: (SavedResult) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(saved: SavedResult) {
            binding.tvItemFoodName.text = saved.foodName
            binding.tvItemDate.text = saved.date
            binding.root.setOnClickListener {
                onItemClick(saved)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
