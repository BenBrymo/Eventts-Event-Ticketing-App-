package com.example.eventts.dataClass

import kotlinx.serialization.Serializable


@Serializable
data class TicketType(
    val name: String,
    val price: Double,
    val availableTickets: Int
)
