package com.smd.studio.personalagenda.ui.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.smd.studio.personalagenda.R
import com.smd.studio.personalagenda.model.Attendee
import com.smd.studio.personalagenda.model.Event
import com.smd.studio.personalagenda.util.Util

class EventDialogFragment : DialogFragment() {

    lateinit var event: Event

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        arguments?.let {
            event = it.getSerializable(Util.EVENT_ARGUMENTS) as Event
        }
        return inflater.inflate(R.layout.event_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title: TextView = view.findViewById(R.id.title)
        val calendar: TextView = view.findViewById(R.id.calendar)
        val time: TextView = view.findViewById(R.id.time)
        val location: TextView = view.findViewById(R.id.location)
        val attendees: TextView = view.findViewById(R.id.attendees)
        val notes: TextView = view.findViewById(R.id.notes)

        title.text = event.title
        title.setTextColor(event.calendarColor)

        calendar.text = event.calendarName
        calendar.setTextColor(event.calendarColor)
        if (1 == event.allDayEvent) {
            time.text = getString(R.string.all_day)
        } else {
            time.text = getString(R.string.event_time, Util.getDate(event.startTime), Util.getDate(event.endTime))
        }

        val locationInfo = if (event.location == "") "N/A" else event.location
        location.text = getString(R.string.event_location, locationInfo)

        var attendeesInfo = ""
        if (event.attendees.isNotEmpty()) {
            for (attendee: Attendee in event.attendees) {
                attendeesInfo = attendeesInfo.plus(
                        if (attendee.name != "") attendee.name.plus(getString(R.string.attendee_separator))
                        else attendee.email.plus(getString(R.string.attendee_separator))
                )
            }
        } else attendeesInfo = "N/A"
        val attendeesInfoLimit = 300
        attendees.text = getString(R.string.event_attendees,
                if (attendeesInfo.length > attendeesInfoLimit) attendeesInfo.take(attendeesInfoLimit).plus("...") else attendeesInfo)

        val notesInfo = if (event.description == "") "N/A" else event.description
        val notesInfoLimit = 100
        notes.text = getString(R.string.event_notes,
                if (notesInfo.length > notesInfoLimit) notesInfo.take(notesInfoLimit).plus("...") else notesInfo)
    }


}