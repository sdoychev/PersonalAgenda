package com.smd.studio.personalagenda.ui.dialog

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.smd.studio.personalagenda.R
import com.smd.studio.personalagenda.adapter.EventItemType
import com.smd.studio.personalagenda.model.Event
import com.smd.studio.personalagenda.util.Util
import java.util.*

class QuickMeetingDialogFragment : DialogFragment() {

    private var prefs: SharedPreferences? = null
    private var savedMeetingDuration: Int = 30
    private val calendar = Calendar.getInstance()
    private var events = arrayListOf<Event>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        arguments?.let {
            @Suppress("UNCHECKED_CAST")
            events = it.getSerializable(Util.QUICK_MEETING_ARGUMENTS) as ArrayList<Event>
        }
        return inflater.inflate(R.layout.quick_meeting_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = this.activity?.getSharedPreferences(Util.AGENDA_APP_SHARED_PREFS, 0)
        savedMeetingDuration = if (prefs == null) 30 else prefs!!.getInt(Util.SHARED_PREFS_MEETING_DURATION_KEY, 30)
        val quickMeeting: TextView = view.findViewById(R.id.quickMeetingValue)

        val quickMeetingStart = getFirstAvailableHour(calendar.timeInMillis)
        val quickMeetingEnd = getPossibleEndHour(quickMeetingStart, savedMeetingDuration)
        checkFreeTimeSlot(quickMeetingStart, quickMeetingEnd, quickMeeting, events, 0)
    }

    private fun getFirstAvailableHour(currentTime: Long): Long {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.timeInMillis = currentTime
        tempCalendar.set(Calendar.MILLISECOND, 0)
        tempCalendar.set(Calendar.SECOND, 0)
        val minutes = tempCalendar.get(Calendar.MINUTE)

        if (minutes in 0..29) {
            tempCalendar.set(Calendar.MINUTE, 30)
        } else {
            tempCalendar.set(Calendar.MINUTE, 0)
            tempCalendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1)
        }
        return tempCalendar.timeInMillis
    }

    private fun getPossibleEndHour(startTime: Long, duration: Int): Long {
        val tempCalendar = Calendar.getInstance()
        tempCalendar.timeInMillis = startTime
        tempCalendar.add(Calendar.MINUTE, duration)
        return tempCalendar.timeInMillis
    }

    private fun checkFreeTimeSlot(timeSlotStart: Long, timeSlotEnd: Long, quickMeetingField: TextView, events: ArrayList<Event>, eventIndex: Int) {
        Log.d("QUICKMEETING", "Start at: $timeSlotStart ___ End at: $timeSlotEnd ___ Index: $eventIndex")
        val event: Event
        if (eventIndex >= events.size) {
            timeSlotFound(quickMeetingField, timeSlotStart)
            return
        } else {
            event = events[eventIndex]
        }
        if (event.type == EventItemType.Event) {
            if (timeSlotEnd <= event.startTime.toLong()) {
                Log.d("QUICKMEETING", "EVENT ${event.title} 111111")
                timeSlotFound(quickMeetingField, timeSlotStart)
                return
            }
        }
        if (event.type == EventItemType.Event) {
            if (event.endTime.toLong() <= timeSlotStart) {
                Log.d("QUICKMEETING", "EVENT ${event.title} 222222")
                checkFreeTimeSlot(timeSlotStart, timeSlotEnd, quickMeetingField, events, eventIndex + 1)
                return
            }
        }
        if (event.type == EventItemType.Event) {
            Log.d("QUICKMEETING", "EVENT ${event.title} 333333")
            val tempTimeSlotEnd = getPossibleEndHour(event.endTime.toLong(), savedMeetingDuration)
            checkFreeTimeSlot(event.endTime.toLong(), tempTimeSlotEnd, quickMeetingField, events, eventIndex + 1)
            return
        }
        checkFreeTimeSlot(timeSlotStart, timeSlotEnd, quickMeetingField, events, eventIndex + 1)
        return
    }

    private fun timeSlotFound(quickMeetingField: TextView, quickMeetingStartTime: Long) {
        quickMeetingField.text = Util.getDate(quickMeetingStartTime.toString())
    }
}