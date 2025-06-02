package com.eatdel.eattoplan.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.ui.map.MapActivity
import com.eatdel.eattoplan.databinding.ActivityResultBinding
import com.eatdel.eattoplan.ui.main.MainActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private var analyzedFoodName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) 인텐트로부터 분석된 결과(음식 이름) 수신
        analyzedFoodName = intent.getStringExtra("foodName") ?: "알 수 없는 음식"

        // 2) 텍스트뷰에 결과 표시
        binding.tvFoodResult.text = "이 음식은 \"$analyzedFoodName\"(으)로 분석되었습니다."

        // 3) 맛집 지도 보기 버튼 클릭
        binding.btnViewMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("foodName", analyzedFoodName)
            startActivity(intent)
        }

        // 4) 저장하기 버튼 (즐겨찾기나 계획 저장 등)
        binding.btnSaveResult.setOnClickListener {
            // TODO: Room DB나 내부 DB에 결과 저장 로직 구현
            // 예: Toast.makeText(this, "\"$analyzedFoodName\" 저장됨", Toast.LENGTH_SHORT).show()
        }

        // 5) 메인으로 돌아가기 버튼 클릭 시 MainActivity로 이동
        binding.btnBackMain.setOnClickListener {
            // 현재 스택에서 ResultActivity를 제거하고 MainActivity로 돌아갑니다.
            val intent = Intent(this, MainActivity::class.java)
            // 필요 시 플래그를 추가하여 기존 스택을 깨끗하게 할 수 있습니다.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 혹은 finish()만 해도 바로 뒤로가기 처리됨
        }
    }
}