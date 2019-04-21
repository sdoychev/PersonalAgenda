package com.smd.studio.personalagenda.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kodmap.library.kmrecyclerviewstickyheader.KmStickyListener
import com.smd.studio.personalagenda.R
import com.smd.studio.personalagenda.model.Event
import com.smd.studio.personalagenda.util.Util

class EventsAdapter(private val eventsList: List<Event>, val eventClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), KmStickyListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EventItemType.Header -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
                HeaderViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.agenda_item, parent, false)
                EventViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == EventItemType.Header) {
            (holder as HeaderViewHolder).bindHeader(holder, eventsList[position])
        } else {
            (holder as EventViewHolder).bindEvent(holder, eventsList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return eventsList[position].type
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return eventsList[itemPosition].type == EventItemType.Header
    }

    override fun getHeaderLayout(headerPosition: Int?): Int {
        return R.layout.header_item
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int? {
        var headerPosition: Int? = 0
        for (i in itemPosition downTo 1) {
            if (isHeader(i)) {
                headerPosition = i
                return headerPosition
            }
        }
        return headerPosition
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val tv: TextView = header.findViewById(R.id.headerTitle)
        tv.text = eventsList[headerPosition].title
    }

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bindEvent(holder: EventViewHolder, event: Event) {
            if (1 == event.allDayEvent) {
                holder.title.text = "${event.title}\nALL DAY"
            } else {
                holder.title.text = "${event.title}\n${Util.getDate(event.startTime)} - ${Util.getDate(event.endTime)}"
            }
            holder.color.setImageDrawable(ColorDrawable(event.calendarColor))
            holder.agendaItem.setOnClickListener { eventClickListener(position) }
        }

        var agendaItem: LinearLayout = view.findViewById(R.id.agendaItem)
        var title: TextView = view.findViewById(R.id.eventTitle)
        var color: ImageView = view.findViewById(R.id.eventColor)
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindHeader(holder: HeaderViewHolder, event: Event) {
            holder.title.text = event.title
        }

        var title: TextView = view.findViewById(R.id.headerTitle)
    }
}