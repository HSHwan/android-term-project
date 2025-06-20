package com.eatdel.eattoplan.data

/**
 * 구글 플레이스 검색 결과 아이템 모델
 */
data class PlaceItem(
    val place_id: String,
    val name: String,
    val address: String,
    val rating: Double?,
    val reviewCount: Int      // ← 리뷰 개수 필드
)