package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.Plan
import com.eatdel.eattoplan.databinding.ItemResultBinding

class ResultAdapter(
    private val items: List<Plan>,
    private val onClick: (Plan) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: Plan) {
            binding.tvItemFoodName.text = plan.name
            binding.tvItemDate.text = plan.meet_date
            binding.root.setOnClickListener { onClick(plan) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
