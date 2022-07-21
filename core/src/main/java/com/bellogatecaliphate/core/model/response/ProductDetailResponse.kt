package com.bellogatecaliphate.core.model.response

data class ProductDetailResponse(
    val category: String?  = null,
    val closingPrice: ClosingPrice?  = null,
    val currentPrice: CurrentPrice?  = null,
    val dayRange: DayRange?  = null,
    val description: String?  = null,
    val displayDecimals: Int?  = null,
    val displayName: String?  = null,
    val favorite: Boolean?  = null,
    val localizedMainTag: LocalizedMainTag?  = null,
    val maxLeverage: Int?  = null,
    val multiplier: Int?  = null,
    val openingHours: OpeningHours?  = null,
    val productMarketStatus: String?  = null,
    val quoteCurrency: String?  = null,
    val securityId: String?  = null,
    val symbol: String?  = null,
    val tags: List<String>?  = null,
    val yearRange: YearRange?  = null
)