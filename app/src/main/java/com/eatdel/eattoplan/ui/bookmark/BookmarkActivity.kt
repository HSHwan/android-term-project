package com.eatdel.eattoplan.ui.bookmark

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.adapter.ResultAdapter
import com.eatdel.eattoplan.data.SavedResult
import com.eatdel.eattoplan.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding

    // TODO: 실제 즐겨찾기 데이터는 DB에서 불러오도록 변경
    private val mockBookmarkList = listOf(
        SavedResult(id = 1, foodName = "된장찌개", date = "2025-05-28 13:00"),
        SavedResult(id = 2, foodName = "피자", date = "2025-05-30 19:45")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기 설정
        setSupportActionBar(binding.toolbarBookmark)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarBookmark.setNavigationOnClickListener { finish() }

        // RecyclerView 세팅
        binding.rvBookmark.layoutManager = LinearLayoutManager(this)
        val adapter = ResultAdapter(mockBookmarkList) { bookmarked ->
            Toast.makeText(this, "${bookmarked.foodName} 즐겨찾기 선택됨", Toast.LENGTH_SHORT).show()
            // TODO: 상세 화면 or MapActivity 등으로 이동
        }
        binding.rvBookmark.adapter = adapter
    }
}