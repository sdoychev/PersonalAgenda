package com.smd.studio.personalagenda.model

data class Event(
        val id: Int,
        val title: String,
        val description: String,
        val startTime: String,
        val endTime: String,
        val location: String,
        val calendarName: String,
        val calendarColor: Int,
        val availability: String,
        val attendees: List<Attendee>)