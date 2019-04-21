package com.smd.studio.personalagenda.ui.dialog

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import com.smd.studio.personalagenda.R
import com.smd.studio.personalagenda.util.Util

class SettingsDialogFragment : DialogFragment() {

    private var prefs: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.settings_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = this.activity?.getSharedPreferences(Util.AGENDA_APP_SHARED_PREFS, 0)
        val meetingSwitch: Switch = view.findViewById(R.id.quickMeetingSwitch)
        val savedMeetingDuration = prefs?.getInt(Util.SHARED_PREFS_MEETING_DURATION_KEY, 30)
        meetingSwitch.isChecked = savedMeetingDuration == 60
        meetingSwitch.setOnCheckedChangeListener { _, isChecked ->
            val meetingDurationValue: Int = if (isChecked) 60 else 30
            prefs?.let {
                val editor = it.edit()
                editor.putInt(Util.SHARED_PREFS_MEETING_DURATION_KEY, meetingDurationValue)
                editor.apply()
            }
        }

        val daysTimespan: EditText = view.findViewById(R.id.eventsTimespanInput)
        val daysTimespanValue = prefs?.getInt(Util.SHARED_PREFS_MEETING_DAYS_TIMESPAN_KEY, 10)
        daysTimespan.setText(daysTimespanValue.toString())
        daysTimespan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == "") {
                    daysTimespan.setText(0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                prefs?.let {
                    val editor = it.edit()
                    if (s != null && s.isNotEmpty()) {
                        editor.putInt(Util.SHARED_PREFS_MEETING_DAYS_TIMESPAN_KEY, s.toString().toInt())
                        editor.apply()
                    }
                }
            }
        })
    }
}