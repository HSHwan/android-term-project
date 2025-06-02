package com.eatdel.eattoplan.util

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.adapter.ResultAdapter
import com.eatdel.eattoplan.data.SavedResult
import com.eatdel.eattoplan.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding

    // TODO: 실제로는 Room DB 또는 SharedPreferences 등에서 불러온 데이터를 사용
    private val mockSavedList = listOf(
        SavedResult(id = 1, foodName = "김밥", date = "2025-06-01 12:30"),
        SavedResult(id = 2, foodName = "치킨", date = "2025-06-02 18:15")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기 설정
        setSupportActionBar(binding.toolbarList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarList.setNavigationOnClickListener { finish() }

        // RecyclerView 세팅
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ResultAdapter(mockSavedList) { savedResult ->
            Toast.makeText(this, "${savedResult.foodName} 클릭됨", Toast.LENGTH_SHORT).show()
            // TODO: 상세 화면(ResultActivity 또는 별도 Activity)으로 이동
        }
        binding.recyclerView.adapter = adapter
    }
}