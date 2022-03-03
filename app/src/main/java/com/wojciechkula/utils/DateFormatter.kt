package com.wojciechkula.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun formatDate(date: Date): String {
        val dateFormater = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormater.format(date)
    }

    fun getCurrentDate(): String {
        val dateFormater = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormater.timeZone = TimeZone.getTimeZone("GMT-04:00")
        return dateFormater.format(Date())
    }

}