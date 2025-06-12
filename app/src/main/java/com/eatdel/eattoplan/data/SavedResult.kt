package com.eatdel.eattoplan.data

data class Plan(
    val place_id: String = "",
    val name:    String = "",
    val address: String = "",
    val memo:    String = "",
    val meet_date:    String = ""
)

data class Bookmark(
    val place_id: String = "",
    val name: String = "",
    val address: String = "",
    val rating: Double = 0.0,
    val phoneNumber: String = ""
)
