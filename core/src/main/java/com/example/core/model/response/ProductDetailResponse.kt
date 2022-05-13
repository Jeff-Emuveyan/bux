package com.example.core.model.response

data class ProductDetailResponse(
    val category: String?,
    val closingPrice: ClosingPrice?,
    val currentPrice: CurrentPrice?,
    val dayRange: DayRange?,
    val description: String?,
    val displayDecimals: Int?,
    val displayName: String?,
    val favorite: Boolean?,
    val localizedMainTag: LocalizedMainTag?,
    val maxLeverage: Int?,
    val multiplier: Int?,
    val openingHours: OpeningHours?,
    val productMarketStatus: String?,
    val quoteCurrency: String?,
    val securityId: String?,
    val symbol: String?,
    val tags: List<String>?,
    val yearRange: YearRange?
)