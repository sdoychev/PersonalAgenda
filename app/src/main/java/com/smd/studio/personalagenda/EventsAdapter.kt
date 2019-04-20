package com.smd.studio.personalagenda

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smd.studio.personalagenda.model.Event

class EventsAdapter(private val eventsList: List<Event>, val eventClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.agenda_item, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.title.text = event.title
        holder.color.setImageDrawable(ColorDrawable(event.calendarColor))
        holder.agendaItem.setOnClickListener { eventClickListener(position) }
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var agendaItem: LinearLayout = view.findViewById(R.id.agendaItem)
        var title: TextView = view.findViewById(R.id.eventTitle)
        var color: ImageView = view.findViewById(R.id.eventColor)

    }
}