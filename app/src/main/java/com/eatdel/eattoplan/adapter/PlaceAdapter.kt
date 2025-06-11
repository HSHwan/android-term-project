import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.data.PlaceItem
import com.eatdel.eattoplan.databinding.ItemPlaceBinding

class PlacesAdapter(
    private var items: List<PlaceItem>,
    private val bookmarkedIds: MutableSet<String>,
    private val savedIds: MutableSet<String>,
    private val onBookmarkClick: (PlaceItem, Boolean) -> Unit,
    private val onSaveClick: (PlaceItem, Boolean) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(val binding: ItemPlaceBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = items[position]
        with(holder.binding) {
            tvName.text = place.name
            tvAddress.text = place.address

            // 1) 아이콘 초기 상태
            val isBook = bookmarkedIds.contains(place.placeId)
            ivBookmark.setImageResource(
                if (isBook) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
            )
            val isSave = savedIds.contains(place.placeId)
            ivSavePlan.setImageResource(
                if (isSave) R.drawable.ic_save_filled else R.drawable.ic_saved
            )

            // 2) 클릭 리스너: 상태 토글 + 호출자에게 알림
            ivBookmark.setOnClickListener {
                val newState = !isBook
                onBookmarkClick(place, newState)
                ivBookmark.setImageResource(
                    if (newState) R.drawable.ic_bookmark_filled
                    else R.drawable.ic_bookmark
                )
            }
            ivSavePlan.setOnClickListener {
                val newState = !isSave
                onSaveClick(place, newState)
                ivSavePlan.setImageResource(
                    if (newState) R.drawable.ic_save_filled
                    else R.drawable.ic_saved
                )
            }
        }
    }

    override fun getItemCount() = items.size

    // 리스트 갱신 메서드
    fun submitList(newItems: List<PlaceItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
