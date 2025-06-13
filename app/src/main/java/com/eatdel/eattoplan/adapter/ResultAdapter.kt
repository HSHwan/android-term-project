package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.Plan
import com.eatdel.eattoplan.databinding.ItemResultBinding

class ResultAdapter(
    private val items: List<Plan>,
    private val onClick:        (Plan) -> Unit,
    private val onRemoveClick:  (Plan) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemResultBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = items[position]
        holder.binding.apply {
            tvItemFoodName.text = plan.name
            tvItemDate.text     = plan.meet_date
            tvItemAddress.text  = plan.address
            tvItemMemo.text     = plan.memo

            // 전체 카드 클릭 → 상세
            root.setOnClickListener { onClick(plan) }
            // 삭제 아이콘 클릭 → 콜백
            ivRemovePlan.setOnClickListener { onRemoveClick(plan) }
        }
    }
}
