package com.eatdel.eattoplan.data

data class Plan(
    val title: String = "",
    val name: String = "",
    val place_id: String = "",
    val meet_date: String = "",
    val contact_info: String = ""
)

data class Bookmark(
    val place_id: String = "",
    val name: String = "",
    val address: String = "",
    val rating: Double = 0.0,
    val phoneNumber: String = ""
)
