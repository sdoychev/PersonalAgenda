package com.smd.studio.personalagenda.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.kodmap.library.kmrecyclerviewstickyheader.KmHeaderItemDecoration
import com.smd.studio.personalagenda.R
import com.smd.studio.personalagenda.adapter.EventItemType
import com.smd.studio.personalagenda.adapter.EventsAdapter
import com.smd.studio.personalagenda.model.Attendee
import com.smd.studio.personalagenda.model.Event
import com.smd.studio.personalagenda.ui.dialog.EventDialogFragment
import com.smd.studio.personalagenda.util.Util
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var agendaList: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var runnable: Runnable

    private var stickyDay: String = ""
    private val eventsList = arrayListOf<Event>()
    private var calendarNames: HashMap<Int, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        agendaList = findViewById(R.id.agendaList)
        emptyView = findViewById(R.id.emptyView)

        setupEventList()
        updateEmptyViewVisibility()
        requestCalendarPermissions()
    }

    private fun setupEventList() {
        eventsAdapter = EventsAdapter(eventsList, eventClickListener = { eventClickListener(it) })
        eventsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateEmptyViewVisibility()
            }
        })
        agendaList.layoutManager = LinearLayoutManager(applicationContext)
        agendaList.itemAnimator = DefaultItemAnimator()
        agendaList.addItemDecoration(KmHeaderItemDecoration(eventsAdapter))
        agendaList.adapter = eventsAdapter
    }

    private fun eventClickListener(eventPosition: Int) {
        val fm = supportFragmentManager
        val dialogFragment = EventDialogFragment()
        val dialogArguments = Bundle()
        dialogArguments.putSerializable(Util.EVENT_ARGUMENTS, eventsList[eventPosition])
        dialogFragment.arguments = dialogArguments
        dialogFragment.show(fm, "EventDialog")
    }

    private fun updateEmptyViewVisibility() {
        if (eventsList.isEmpty()) {
            agendaList.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            agendaList.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    private fun requestCalendarPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {
                AlertDialog.Builder(this).setMessage(R.string.enable_permissions)
                        .setPositiveButton(R.string.allow) { _, _ -> requestPermissions() }
                        .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), Util.CALENDAR_PERMISSIONS)
            }
        } else {
            loadCalendarEvents()
        }
    }

    private fun requestPermissions() {
        val remainingPermissions = arrayListOf(Manifest.permission.READ_CALENDAR)
        for (permission in remainingPermissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission)
            }
        }
        requestPermissions(remainingPermissions.toTypedArray(), Util.CALENDAR_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Util.CALENDAR_PERMISSIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadCalendarEvents()
            }
        }
    }

    private fun loadCalendarEvents() {
        val handler = Handler()
        runnable = Runnable {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                // Dates parameters
                val calendar = Calendar.getInstance()
                val startDay = calendar.timeInMillis
                //TODO CUSTOMIZABLE PARAM
                calendar.add(Calendar.DAY_OF_YEAR, 10)
                val endDay = calendar.timeInMillis
                val attendeeList = ArrayList<Attendee>()

                // Query parameters
                val calendarSortOrder = CalendarContract.Calendars._ID + " ASC"
                val eventsSortOrder = CalendarContract.Events.DTSTART + " ASC"
                val eventsSelection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ?"
                val eventsSelectionArgs = arrayOf(java.lang.Long.toString(startDay), java.lang.Long.toString(endDay))
                val attendeeSelection = CalendarContract.Attendees.EVENT_ID + " = ?"

                // Calendars query
                val calendarCursor: Cursor? = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, Util.CALENDAR_PROJECTION, null, null, calendarSortOrder)
                calendarCursor?.let {
                    while (calendarCursor.moveToNext()) {
                        calendarNames[calendarCursor.getInt(Util.PROJECTION_CALENDAR_ID_INDEX)] = calendarCursor.getString(Util.PROJECTION_CALENDAR_DISPLAY_NAME_INDEX)
                    }
                    calendarCursor.close()
                }

                // Events query
                val eventCursor: Cursor? = contentResolver.query(CalendarContract.Events.CONTENT_URI, Util.EVENT_PROJECTION, eventsSelection, eventsSelectionArgs, eventsSortOrder)
                eventCursor?.let {
                    eventCursor.moveToFirst()
                    do {
                        attendeeList.clear()
                        // Attendee query
                        val attendeeCursor: Cursor? = contentResolver.query(CalendarContract.Attendees.CONTENT_URI, Util.ATTENDEE_PROJECTION, attendeeSelection,
                                arrayOf(eventCursor.getInt(Util.PROJECTION_EVENT_ID_INDEX).toString()), null)
                        attendeeCursor?.let {
                            if (attendeeCursor.count > 0) {
                                attendeeCursor.moveToFirst()
                                do {
                                    attendeeList.add(Attendee(attendeeCursor.getString(Util.PROJECTION_ATTENDEE_NAME_INDEX),
                                            attendeeCursor.getString(Util.PROJECTION_ATTENDEE_EMAIL_INDEX)))
                                } while (attendeeCursor.moveToNext())
                                attendeeCursor.close()
                            }
                        }
                        val event = Event(
                                eventCursor.getInt(Util.PROJECTION_EVENT_ID_INDEX),
                                EventItemType.Event,
                                eventCursor.getString(Util.PROJECTION_EVENT_TITLE_INDEX),
                                eventCursor.getString(Util.PROJECTION_EVENT_DESCRIPTION_INDEX),
                                eventCursor.getString(Util.PROJECTION_EVENT_DATE_START_INDEX),
                                eventCursor.getString(Util.PROJECTION_EVENT_DATE_END_INDEX),
                                eventCursor.getString(Util.PROJECTION_EVENT_LOCATION_INDEX),
                                calendarNames[eventCursor.getInt(Util.PROJECTION_EVENT_CALENDAR_ID_INDEX)].toString(),
                                eventCursor.getInt(Util.PROJECTION_EVENT_DISPLAY_COLOR_INDEX),
                                eventCursor.getString(Util.PROJECTION_EVENT_AVAILABILITY_INDEX),
                                eventCursor.getInt(Util.PROJECTION_EVENT_ALL_DAY_INDEX),
                                attendeeList.toList()
                        )
                        val currentStickyDate = Util.getStickyDate(event.startTime)
                        if (stickyDay == "" || stickyDay != currentStickyDate) {
                            stickyDay = currentStickyDate
                            eventsList.add(Event(Util.STICKY_HEADER_ID, EventItemType.Header, stickyDay, "",
                                    "", "", "", "", 0, "", 0, emptyList()))
                        }
                        eventsList.add(event)
                        eventsAdapter.notifyDataSetChanged()
                        updateEmptyViewVisibility()
                    } while (eventCursor.moveToNext())
                    eventCursor.close()
                }
            }
        }
        handler.post(runnable)
    }
}
