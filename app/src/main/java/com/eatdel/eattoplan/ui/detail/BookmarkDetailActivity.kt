// app/src/main/java/com/eatdel/eattoplan/ui/bookmark/BookmarkDetailActivity.kt
package com.eatdel.eattoplan.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.databinding.ActivityBookmarkDetailBinding

class BookmarkDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_UID         = "extra_uid"
        const val EXTRA_RESTAURANT  = "extra_restaurant"
        const val EXTRA_PLACE_ID    = "extra_place_id"
        const val EXTRA_RATE        = "extra_rate"
    }

    private lateinit var binding: ActivityBookmarkDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 인텐트에서 데이터 받기
        val restaurant = intent.getStringExtra(EXTRA_RESTAURANT) ?: ""
        val placeId    = intent.getLongExtra(EXTRA_PLACE_ID, 0L)
        val rate       = intent.getIntExtra(EXTRA_RATE, 0)

        binding.tvDetailRestaurant.text = "맛집: $restaurant"
        binding.tvDetailPlaceId.text    = "Place ID: $placeId"
        binding.tvDetailRate.text       = "평점: $rate"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
