package com.eatdel.eattoplan.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.databinding.ActivityPlanDetailBinding

class PlanDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent 에서 값 꺼내기
        val name    = intent.getStringExtra(EXTRA_RESTAURANT_NAME).orEmpty()
        val address = intent.getStringExtra(EXTRA_ADDRESS      ).orEmpty()
        val meet_date    = intent.getStringExtra(EXTRA_MEET_DATE    ).orEmpty()
        val memo    = intent.getStringExtra(EXTRA_MEMO         ).orEmpty()

        // 화면에 바인딩
        binding.tvDetailRestaurant.text = "음식점: $name"
        binding.tvDetailAddress.text    = "주소: $address"
        binding.tvDetailMeetDate.text   = "모임일: $meet_date"
        binding.tvDetailMemo.text       = "메모: $memo"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_RESTAURANT_NAME = "extra_restaurant_name"
        const val EXTRA_ADDRESS         = "extra_address"
        const val EXTRA_MEET_DATE       = "extra_meet_date"
        const val EXTRA_MEMO            = "extra_memo"
    }
}
