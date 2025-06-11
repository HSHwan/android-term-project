package com.eatdel.eattoplan.data

data class Plan(
    val uid: Long = 0L,
    val title: String = "",
    val restaurant_name: String = "",
    val place_id: Long = 0L,
    val meet_date: String = "",
    val memo: String = ""
)

data class Bookmark(
    val uid: Long = 0L,
    val restaurant_name: String = "",
    val place_id: Long = 0L,
    val rate: Int = 0
)
