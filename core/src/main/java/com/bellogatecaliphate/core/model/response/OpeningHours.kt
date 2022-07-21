package com.bellogatecaliphate.core.model.response

data class OpeningHours(
    val timezone: String?,
    val weekDays: List<List<WeekDay>>?
)