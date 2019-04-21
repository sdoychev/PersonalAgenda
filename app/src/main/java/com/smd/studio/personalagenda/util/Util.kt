package com.smd.studio.personalagenda.util

import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {
        //General constants
        const val STICKY_HEADER_ID: Int = 10000
        const val EVENT_ARGUMENTS: String = "EVENT_ARGUMENTS"
        const val QUICK_MEETING_ARGUMENTS: String = "QUICK_MEETING_ARGUMENTS"

        // Shared Preferences
        const val AGENDA_APP_SHARED_PREFS: String = "AGENDA_APP_SHARED_PREFS"
        const val SHARED_PREFS_MEETING_DURATION_KEY: String = "SHARED_PREFS_MEETING_DURATION_KEY"
        const val SHARED_PREFS_MEETING_DAYS_TIMESPAN_KEY: String = "SHARED_PREFS_MEETING_DAYS_TIMESPAN_KEY"

        // Permissions constants
        const val CALENDAR_PERMISSIONS: Int = 0

        // Calendar Projection Array
        val CALENDAR_PROJECTION: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,                         // 0
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME        // 1
        )

        // Event Projection Array
        val EVENT_PROJECTION: Array<String> = arrayOf(
                CalendarContract.Events._ID,                            // 0
                CalendarContract.Events.CALENDAR_ID,                    // 1
                CalendarContract.Events.DISPLAY_COLOR,                  // 2
                CalendarContract.Events.TITLE,                          // 3
                CalendarContract.Events.DESCRIPTION,                    // 4
                CalendarContract.Events.DTSTART,                        // 5
                CalendarContract.Events.DTEND,                          // 6
                CalendarContract.Events.EVENT_LOCATION,                 // 7
                CalendarContract.Events.AVAILABILITY,                   // 8
                CalendarContract.Events.ALL_DAY                         // 9
        )

        // Event Projection Array
        val ATTENDEE_PROJECTION: Array<String> = arrayOf(
                CalendarContract.Attendees.ATTENDEE_NAME,               // 0
                CalendarContract.Attendees.ATTENDEE_EMAIL               // 1
        )

        // Projection arrays indices
        const val PROJECTION_CALENDAR_ID_INDEX: Int = 0
        const val PROJECTION_CALENDAR_DISPLAY_NAME_INDEX: Int = 1
        const val PROJECTION_EVENT_ID_INDEX: Int = 0
        const val PROJECTION_EVENT_CALENDAR_ID_INDEX: Int = 1
        const val PROJECTION_EVENT_DISPLAY_COLOR_INDEX: Int = 2
        const val PROJECTION_EVENT_TITLE_INDEX: Int = 3
        const val PROJECTION_EVENT_DESCRIPTION_INDEX: Int = 4
        const val PROJECTION_EVENT_DATE_START_INDEX: Int = 5
        const val PROJECTION_EVENT_DATE_END_INDEX: Int = 6
        const val PROJECTION_EVENT_LOCATION_INDEX: Int = 7
        const val PROJECTION_EVENT_AVAILABILITY_INDEX: Int = 8
        const val PROJECTION_EVENT_ALL_DAY_INDEX: Int = 9
        const val PROJECTION_ATTENDEE_NAME_INDEX: Int = 0
        const val PROJECTION_ATTENDEE_EMAIL_INDEX: Int = 1

        // Get the time in human-readable format
        fun getDate(milliSeconds: String): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds.toLong()
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        }

        fun getStickyDate(milliSeconds: String): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds.toLong()
            return SimpleDateFormat("EEEE, MM/dd/yyyy", Locale.getDefault()).format(calendar.time)
        }
    }
}