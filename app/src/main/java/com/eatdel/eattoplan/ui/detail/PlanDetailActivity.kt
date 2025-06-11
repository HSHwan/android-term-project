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

        // 1) 새로 추가한 toolbarDetail을 ActionBar로 설정
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 2) 데이터 바인딩
        binding.tvDetailTitle.text =
            intent.getStringExtra(EXTRA_TITLE) ?: "제목 없음"
        binding.tvDetailRestaurant.text =
            "음식점: " + (intent.getStringExtra(EXTRA_RESTAURANT_NAME) ?: "")
        binding.tvDetailPlaceId.text =
            "place_id: " + intent.getLongExtra(EXTRA_PLACE_ID, 0L).toString()
        binding.tvDetailMeetDate.text =
            "모임일: " + (intent.getStringExtra(EXTRA_MEET_DATE) ?: "")
        binding.tvDetailContact.text =
            "연락처: " + (intent.getStringExtra(EXTRA_CONTACT_INFO) ?: "")
    }

    // 툴바 뒤로가기 아이콘 클릭 시
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_UID             = "extra_uid"
        const val EXTRA_TITLE           = "extra_title"
        const val EXTRA_RESTAURANT_NAME = "extra_restaurant_name"
        const val EXTRA_PLACE_ID        = "extra_place_id"
        const val EXTRA_MEET_DATE       = "extra_meet_date"
        const val EXTRA_CONTACT_INFO    = "extra_contact_info"
    }
}
