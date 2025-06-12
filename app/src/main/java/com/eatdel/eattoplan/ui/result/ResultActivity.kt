// app/src/main/java/com/eatdel/eattoplan/ui/result/ResultActivity.kt
package com.eatdel.eattoplan.ui.result

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.data.Plan
import com.eatdel.eattoplan.databinding.ActivityResultBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
                    Toast.makeText(this, "저장 성공!", Toast.LENGTH_SHORT).show()
                    finish()  // 저장 후 뒤로
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
