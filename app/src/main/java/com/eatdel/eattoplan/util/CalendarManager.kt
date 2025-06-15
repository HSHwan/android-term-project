package com.eatdel.eattoplan.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CalendarManager(private val context: Context) {
    private fun getDefaultCalendarId(): Long {
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY
        )

        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            "${CalendarContract.Calendars.IS_PRIMARY} = 1",
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getLong(0)
            }
        }

        return 1L // 기본값
    }

    fun createRestaurantEvent(
        restaurantName: String,
        memo: String,
        date: String, // "yyyy-MM-dd" 형식
    ): Boolean {
        val contentResolver = context.contentResolver

        // 날짜 파싱
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val eventDate = dateFormat.parse(date) ?: return false
        val calendarId = getDefaultCalendarId()

        val calendar = Calendar.getInstance().apply {
            time = eventDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTimeMillis = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endTimeMillis = calendar.timeInMillis

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId) // 필수 필드
            put(CalendarContract.Events.TITLE, "🍽️ $restaurantName")
            put(CalendarContract.Events.DESCRIPTION, memo)
            put(CalendarContract.Events.DTSTART, startTimeMillis)
            put(CalendarContract.Events.DTEND, endTimeMillis)
            put(CalendarContract.Events.ALL_DAY, 1) // 종일 일정 설정
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        return uri != null
    }

    fun deleteRestaurantEventByName(name: String): Boolean {
        val contentResolver = context.contentResolver
        val title = "🍽️ $name"

        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.ALL_DAY
        )

        val selection = "${CalendarContract.Events.TITLE} = ? AND " +
                "${CalendarContract.Events.ALL_DAY} = 1"

        val selectionArgs = arrayOf(
            title
        )

        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var deletedCount = 0

        cursor?.use {
            while (it.moveToNext()) {
                val eventId = it.getLong(0)
                val eventTitle = it.getString(1)

                val deleteUri = ContentUris.withAppendedId(
                    CalendarContract.Events.CONTENT_URI,
                    eventId
                )

                val rowsDeleted = contentResolver.delete(deleteUri, null, null)
                if (rowsDeleted > 0) {
                    deletedCount++
                    Log.d("Calendar", "삭제된 일정: $eventTitle")
                }
            }
        }

        return deletedCount > 0
    }

}