package com.eatdel.eattoplan.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eatdel.eattoplan.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // SupportMapFragment 가져오기 및 비동기 처리
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        // 예시: 서울 시청 위치로 카메라 이동
        val seoulCityHall = LatLng(37.5665, 126.9780)
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulCityHall, 13f))

        // TODO: ML 분석 결과(인텐트로 넘어온 음식 종류나 좌표 정보)를 바탕으로
        // 주변 맛집 위치 목록을 API(예: Google Places API) 등으로 조회한 뒤,
        // 아래와 같이 마커를 찍어준다.

        // 샘플 마커 추가 (예시 데이터)
        val sampleRestaurant1 = LatLng(37.5651, 126.9895)
        gMap.addMarker(MarkerOptions().position(sampleRestaurant1).title("맛집 A"))
        val sampleRestaurant2 = LatLng(37.5700, 126.9820)
        gMap.addMarker(MarkerOptions().position(sampleRestaurant2).title("맛집 B"))
    }
}