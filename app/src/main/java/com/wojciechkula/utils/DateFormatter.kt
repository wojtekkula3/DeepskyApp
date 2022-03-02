package com.wojciechkula.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun formatDate(date: Date): String {
        val dateFormater = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormater.format(date)
    }
}