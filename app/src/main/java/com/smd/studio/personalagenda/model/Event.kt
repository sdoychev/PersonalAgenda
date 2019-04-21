package com.smd.studio.personalagenda.model

import java.io.Serializable

data class Event(
        val id: Int,
        val type: Int,
        val title: String,
        val description: String,
        val startTime: String,
        val endTime: String,
        val location: String,
        val calendarName: String,
        val calendarColor: Int,
        val availability: String,
        val allDayEvent: Int,
        val attendees: List<Attendee>) : Serializable