// app/src/main/java/com/eatdel/eattoplan/ui/places/PlacesSearchActivity.kt
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
import com.eatdel.eattoplan.adapter.PlaceAdapter
import com.eatdel.eattoplan.data.PlaceItem
import com.eatdel.eattoplan.databinding.ActivityPlacesSearchBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: PlaceAdapter

    private val httpClient = OkHttpClient()
    private val apiKey by lazy { getString(R.string.google_maps_key) }

    // Firebase
    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("로그인이 필요합니다")

    // 로컬 상태
    private val bookmarkedIds = mutableSetOf<String>()
    private val savedIds      = mutableSetOf<String>()

    // 위치 권한 런처
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            binding.etKeyword.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
                ?.let { searchNearbyPlaces(it) }
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Places SDK 초기화
        Places.initialize(applicationContext, apiKey)
        placesClient = Places.createClient(this)

        // 2) FusedLocation 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 3) 어댑터 생성 및 RecyclerView 세팅
        adapter = PlaceAdapter(
            items           = emptyList(),
            bookmarkedIds   = bookmarkedIds,
            savedIds        = savedIds,
            onBookmarkClick = ::handleBookmarkToggle,
            onSaveClick     = ::handleSaveToggle,
            onItemClick     = { place ->
                val starText = place.rating?.let { String.format("%.1f", it) } ?: "정보 없음"
                val countText = "${place.reviewCount}개 리뷰"
                Toast.makeText(
                    this,
                    "${place.name}\n별점: $starText\n$countText",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        binding.rvPlaces.layoutManager = LinearLayoutManager(this)
        binding.rvPlaces.adapter = adapter

        // 4) 기존 북마크·플랜 로드
        db.collection("users").document(userId)
            .collection("bookmarks")
            .get()
            .addOnSuccessListener { snap ->
                snap.documents.forEach { bookmarkedIds.add(it.id) }
                adapter.notifyDataSetChanged()
            }

        db.collection("users").document(userId)
            .collection("plans")
            .get()
            .addOnSuccessListener { snap ->
                snap.documents.forEach { savedIds.add(it.id) }
                adapter.notifyDataSetChanged()
            }

        // 5) 검색 버튼 클릭 리스너
        binding.btnSearch.setOnClickListener {
            binding.etKeyword.text.toString().trim().takeIf { it.isNotEmpty() }
                ?.let { searchNearbyPlaces(it) }
        }
    }

    /** 현재 위치를 가져와서 Nearby Search 호출 */
    private fun searchNearbyPlaces(keyword: String) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
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
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    /** HTTP Nearby Search → 필터링 → 리스트 갱신 */
    private fun fetchNearbyPlaces(lat: Double, lng: Double, keyword: String) {
        val url = buildString {
            append("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
            append("?location=$lat,$lng&radius=2000&type=restaurant")
            append("&keyword=${URLEncoder.encode(keyword, "UTF-8")}")
            append("&language=ko&key=$apiKey")
        }

        //리뷰 200개 넘는 것 중에 별점 상위 5개 식당
        CoroutineScope(Dispatchers.IO).launch {
            val resp = httpClient.newCall(Request.Builder().url(url).build())
                .execute().body?.string().orEmpty()
            val places = parsePlacesHttp(resp)
                .filter { it.reviewCount >= 200 }
                .sortedByDescending { it.rating ?: 0.0 }
                .take(5)
            runOnUiThread {
                adapter.submitList(places)
            }
        }
    }

    /** JSON → PlaceItem 리스트 */
    private fun parsePlacesHttp(json: String): List<PlaceItem> {
        val arr = JSONObject(json).optJSONArray("results") ?: return emptyList()
        return List(arr.length()) { i ->
            arr.optJSONObject(i)!!.let { o ->
                PlaceItem(
                    place_id     = o.optString("place_id"),
                    name        = o.optString("name"),
                    address     = o.optString("vicinity"),
                    rating      = o.optDouble("rating").takeIf { o.has("rating") },
                    reviewCount = o.optInt("user_ratings_total", 0)
                )
            }
        }
    }

    /** 즐겨찾기 토글: Place Details API로 전화번호까지 받아와서 저장/삭제 */
    private fun handleBookmarkToggle(place: PlaceItem, add: Boolean) {
        val docRef = db.collection("users")
            .document(userId)
            .collection("bookmarks")
            .document(place.place_id)

        if (add) {
            // 상세정보(전화번호) 조회
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING
            )
            val request = FetchPlaceRequest.builder(place.place_id, fields).build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { resp ->
                    val p = resp.place
                    val data = mapOf(
                        "place_id"     to p.id,
                        "name"        to p.name,
                        "address"     to place.address,
                        "rating"      to (p.rating ?: 0.0),
                        "phoneNumber" to (p.phoneNumber ?: "")
                    )
                    docRef.set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show()
                            bookmarkedIds.add(place.place_id)
                            val idx = adapter.currentItems.indexOf(place)
                            if (idx >= 0) adapter.notifyItemChanged(idx)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "상세조회 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            docRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "즐겨찾기에서 제거되었습니다", Toast.LENGTH_SHORT).show()
                    bookmarkedIds.remove(place.place_id)
                    val idx = adapter.currentItems.indexOf(place)
                    if (idx >= 0) adapter.notifyItemChanged(idx)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "제거 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /** 계획 저장 토글 (기존 로직 그대로) */
    private fun handleSaveToggle(place: PlaceItem, add: Boolean) {
        val doc = db.collection("users")
            .document(userId)
            .collection("plans")
            .document(place.place_id)

        if (add) {
            doc.set(place)
                .addOnSuccessListener {
                    Toast.makeText(this, "내 계획에 추가되었습니다", Toast.LENGTH_SHORT).show()
                    savedIds.add(place.place_id)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "계획 저장 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            doc.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "내 계획에서 제거되었습니다", Toast.LENGTH_SHORT).show()
                    savedIds.remove(place.place_id)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "계획 제거 실패", Toast.LENGTH_SHORT).show()
                }
        }
        adapter.notifyDataSetChanged()
    }
}
