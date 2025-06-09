package com.eatdel.eattoplan.ui.places

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.adapter.PlacesAdapter
import com.eatdel.eattoplan.data.PlaceItem
import com.eatdel.eattoplan.databinding.ActivityPlacesSearchBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

class PlacesSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlacesSearchBinding
    private val client = OkHttpClient()
    private val apiKey by lazy { getString(R.string.google_maps_key) }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 초기화
        binding.rvPlaces.layoutManager = LinearLayoutManager(this)
        binding.rvPlaces.adapter = PlacesAdapter(emptyList()) { place ->
            // 아이템 클릭 시 동작 (예: 상세 뷰)
            Toast.makeText(this, "${place.name}\n${place.address}", Toast.LENGTH_SHORT).show()
        }

        // 검색 버튼 클릭
        binding.btnSearch.setOnClickListener {
            val keyword = binding.etKeyword.text.toString().trim()
            if (keyword.isEmpty()) {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                searchNearby(keyword)
            }
        }

        // 위치 권한 요청
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun searchNearby(keyword: String) {
        // 권한 재체크
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "위치 권한이 허용되어야 검색할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val fused = LocationServices.getFusedLocationProviderClient(this)
        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { loc ->
                if (loc == null) {
                    Toast.makeText(
                        this,
                        "위치 정보를 가져올 수 없습니다. GPS를 켜고 다시 시도하세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    promptEnableGps()
                    return@addOnSuccessListener
                }

                val lat = loc.latitude
                val lng = loc.longitude
                fetchPlaces(lat, lng, keyword)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "위치 조회 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("PlacesSearch", "Location failure", e)
            }
    }

    private fun fetchPlaces(lat: Double, lng: Double, keyword: String) {
        val encoded = URLEncoder.encode(keyword, "UTF-8")
        val url = buildString {
            append("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
            append("?location=$lat,$lng")
            append("&radius=2000")
            append("&keyword=$encoded")
            append("&type=restaurant")        // 식당 위주로
            append("&language=ko")            // 한글 응답
            append("&region=kr")              // 한국 지역 바이어스
            append("&key=$apiKey")
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(Request.Builder().url(url).build())
                    .execute()
                    .body?.string()
                    ?: ""
                val list = parsePlaces(response)
                runOnUiThread {
                    (binding.rvPlaces.adapter as PlacesAdapter).submitList(list)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@PlacesSearchActivity,
                        "검색 중 오류: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("PlacesSearch", "Fetch error", e)
            }
        }
    }

    private fun parsePlaces(json: String): List<PlaceItem> {
        val results = JSONObject(json).optJSONArray("results") ?: return emptyList()
        val out = mutableListOf<PlaceItem>()
        for (i in 0 until results.length()) {
            results.optJSONObject(i)?.let { o ->
                val name = o.optString("name")
                val address = o.optString("vicinity")
                out += PlaceItem(name = name, address = address)
            }
        }
        return out
    }

    private fun promptEnableGps() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}
