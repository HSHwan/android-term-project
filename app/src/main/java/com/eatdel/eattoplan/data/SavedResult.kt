package com.eatdel.eattoplan.data

data class SavedResult(
    val id: Long = 0L,
    val foodName: String,
    val date: String // 저장된 날짜 (예: "2025-06-02 14:30")
    // TODO: 필요 시 좌표, 이미지 URI 등 추가 필드
)