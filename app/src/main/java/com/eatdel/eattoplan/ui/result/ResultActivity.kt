package com.eatdel.eattoplan.ui.result

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.data.Plan
import com.eatdel.eattoplan.databinding.ActivityResultBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 예시: 인텐트로부터 받아온 값
        //val uid             = intent.getLongExtra("place_id", 0L)
        //val title           = intent.getStringExtra("title") ?: ""
        val restaurantName  = intent.getStringExtra("restaurant_name") ?: ""
        val placeId         = intent.getStringExtra("place_id") ?: ""
        val meetDate        = intent.getStringExtra("meet_date") ?: ""
       // val contactInfo     = intent.getStringExtra("contact_info") ?: ""
        val memo            = intent.getStringExtra("memo") ?: ""
        val address         = intent.getStringExtra("address") ?: ""

        // 화면에 일단 보여주기
        binding.tvFoodResult.text = "제목: $title\n음식점: $restaurantName\n모임일: $meetDate"

        binding.btnSaveResult.setOnClickListener {
            val plan = Plan(
                //uid             = uid,
                //title           = title,
                name = restaurantName,
                place_id        = placeId,
                meet_date       = meetDate,
                //contact_info    = contactInfo
                memo          = memo,
                address = address
            )

            db.collection("foodplan")
                .add(plan)
                .addOnSuccessListener {
                    addCalendarEvent(title, meetDate, restaurantName, memo)
                    Toast.makeText(this, "저장 성공!", Toast.LENGTH_SHORT).show()
                    finish()  // 저장 후 뒤로
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addCalendarEvent(
        title: String,
        startTimeString: String,
        location: String,
        memo: String
    ) {
        val startTime = parseStringToMillis(startTimeString)
        val endTime = startTime + (60 * 60 * 1000) // 1시간 후

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            putExtra(CalendarContract.Events.DESCRIPTION, memo)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
        else {
            Toast.makeText(this, "캘린더 앱을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseStringToMillis(dateTimeString: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
