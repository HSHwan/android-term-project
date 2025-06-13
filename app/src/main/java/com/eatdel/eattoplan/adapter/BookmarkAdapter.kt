package com.eatdel.eattoplan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.data.Bookmark
import com.eatdel.eattoplan.databinding.ItemBookmarkBinding
import com.eatdel.eattoplan.R

class BookmarkAdapter(
    private var items: List<Bookmark> = emptyList(),
    private val savedIds: MutableSet<String>,               // plans 에 저장된 placeId 집합
    private val onUnbookmarkClick: (Bookmark) -> Unit,      // 즐겨찾기 해제 콜백
    private val onPlanToggle: (Bookmark, Boolean) -> Unit   // 계획 저장/해제 콜백
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    inner class BookmarkViewHolder(val binding: ItemBookmarkBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    //
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bm = items[position]
        with(holder.binding) {
            tvName.text    = bm.name
            tvRating.text  = String.format("%.1f", bm.rating)
            tvAddress.text = bm.address
            tvPhone.text   = bm.phoneNumber

            // 지워진 후에는 리스트에서 삭제
            ivUnbookmark.setOnClickListener {
                onUnbookmarkClick(bm)
            }
            // 3. 계획 저장 아이콘 상태
            val isPlanned = savedIds.contains(bm.place_id)
            ivSavePlan.setImageResource(
                if (isPlanned) R.drawable.ic_plan_delete
                else R.drawable.ic_plan_add
            )
            // 4) 계획 저장/해제 클릭
            ivSavePlan.setOnClickListener {
                onPlanToggle(bm, !isPlanned)
            }
        }
    }

    /** 외부에서 데이터 갱신할 때 호출 */
    fun submitList(newItems: List<Bookmark>) {
        items = newItems
        notifyDataSetChanged()
    }

    /** 특정 아이템만 리스트에서 제거(애니메이션 없이) */
    fun removeItem(bm: Bookmark) {
        val idx = items.indexOf(bm)
        if (idx >= 0) {
            items = items.toMutableList().apply { removeAt(idx) }
            notifyItemRemoved(idx)
        }
    }

    fun updatePlanState(placeId: String, added: Boolean) {
        if (added) savedIds.add(placeId) else savedIds.remove(placeId)
        // 변경된 아이템 하나만 갱신
        val idx = items.indexOfFirst { it.place_id == placeId }
        if (idx >= 0) notifyItemChanged(idx)
    }

}
