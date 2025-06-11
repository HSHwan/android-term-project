package com.eatdel.eattoplan.ui.places

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatdel.eattoplan.R
import com.eatdel.eattoplan.adapter.PlacesAdapter
import com.eatdel.eattoplan.data.PlaceItem
import com.eatdel.eattoplan.databinding.ActivityPlacesSearchBinding
import com.google.android.gms.location.FusedLocationProviderClient
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val httpClient = OkHttpClient()
    private val apiKey by lazy { getString(R.string.google_maps_key) }

    // 1) 권한 런처: 허용되면 즉시 검색
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // 이전에 입력한 키워드가 남아있다면 재검색
            binding.etKeyword.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let {
                searchNearbyPlaces(it)
            }
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2) FusedLocation 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 3) RecyclerView 세팅
        binding.rvPlaces.layoutManager = LinearLayoutManager(this)
        binding.rvPlaces.adapter = PlacesAdapter(emptyList()) { place ->
            Toast.makeText(this, "${place.name}\n${place.address}", Toast.LENGTH_SHORT).show()
            fetchPlaceDetailsHttp(place.placeId)
        }

        // 4) 검색 버튼 클릭
        binding.btnSearch.setOnClickListener {
            val keyword = binding.etKeyword.text.toString().trim()
            if (keyword.isEmpty()) {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                searchNearbyPlaces(keyword)
            }
        }
    }

    /** 현재 위치를 가져와서 HTTP Nearby Search 수행 */
    private fun searchNearbyPlaces(keyword: String) {
        // 5) 위치 권한 체크
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        // 6) 현재 위치 조회
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { loc: Location? ->
            if (loc != null) {
                fetchNearbyPlaces(loc.latitude, loc.longitude, keyword)
            } else {
                Toast.makeText(
                    this,
                    "위치 정보를 가져올 수 없습니다. GPS를 켜고 다시 시도하세요.",
                    Toast.LENGTH_SHORT
                ).show()
                promptEnableGps()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "위치 조회 실패: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** OkHttp + Coroutine 으로 Nearby Search API 호출 */
    private fun fetchNearbyPlaces(lat: Double, lng: Double, keyword: String) {
        val encoded = URLEncoder.encode(keyword, "UTF-8")
        val url = buildString {
            append("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
            append("?location=$lat,$lng")
            append("&radius=2000")               // 반경 2km
            append("&type=restaurant")           // 식당만
            append("&keyword=$encoded")          // 키워드 필터
            append("&language=ko")               // 한국어 결과
            append("&key=$apiKey")
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = httpClient.newCall(Request.Builder().url(url).build())
                    .execute()
                    .body?.string().orEmpty()
                val list = parsePlacesHttp(resp)
                // UI 스레드에서 RecyclerView 업데이트
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
            }
        }
    }

    /** JSON 파싱 */
    private fun parsePlacesHttp(json: String): List<PlaceItem> {
        val arr = JSONObject(json).optJSONArray("results") ?: return emptyList()
        val out = mutableListOf<PlaceItem>()
        for (i in 0 until arr.length()) {
            arr.optJSONObject(i)?.let { o ->
                out += PlaceItem(
                    placeId = o.optString("place_id"),
                    name = o.optString("name"),
                    address = o.optString("vicinity")
                )
            }
        }
        return out
    }

    /** Http로 식당 정보 파싱 */
    private fun fetchPlaceDetailsHttp(placeId: String) {
        // 요청할 필드들
        val fields = listOf(
            "name",
            "rating",
            "formatted_phone_number",
            "formatted_address"
        ).joinToString(",")

        val url = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?place_id=$placeId" +
                "&fields=$fields" +
                "&language=ko" +
                "&key=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = httpClient.newCall(Request.Builder().url(url).build())
                    .execute().body?.string().orEmpty()
                val result = JSONObject(resp).getJSONObject("result")
                val name    = result.optString("name")
                val rating  = result.optDouble("rating", 0.0)
                val phone   = result.optString("formatted_phone_number")
                val address = result.optString("formatted_address")

                runOnUiThread {
                    // 예: 다이얼로그 또는 새로운 Activity로 보여주기
                    Toast.makeText(
                        this@PlacesSearchActivity,
                        "이름: $name\n별점: $rating\n전화: $phone",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@PlacesSearchActivity,
                        "상세조회 실패: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    /** GPS 설정 화면으로 이동 */
    private fun promptEnableGps() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
}
