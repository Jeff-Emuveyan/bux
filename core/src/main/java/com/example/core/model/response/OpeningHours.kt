package com.example.core.model.response

data class OpeningHours(
    val timezone: String?,
    val weekDays: List<List<WeekDay>>?
)