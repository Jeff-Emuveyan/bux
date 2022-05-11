package com.example.core.model.network

data class OpeningHours(
    val timezone: String,
    val weekDays: List<List<WeekDay>>
)