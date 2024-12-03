package com.example.mob_dev_course.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.fragments.DetailedPlan

class ScheduleAdapter(
    private var data: List<ScheduleItem>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    data class ScheduleItem(val time: String, val title: String, val description: String)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.scheduleTime)
        val titleText: TextView = view.findViewById(R.id.scheduleTitle)
        val descriptionText: TextView = view.findViewById(R.id.scheduleDescription)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDetailedPlan(data[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.timeText.text = item.time
        holder.titleText.text = item.title
        holder.descriptionText.text = item.description
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<ScheduleItem>) {
        data = newData
        notifyDataSetChanged()
    }

    private fun showDetailedPlan(item: ScheduleItem) {
        val dialog = DetailedPlan.newInstance(
            title = item.title,
            description = item.description,
            time = item.time
        )

        dialog.setListener(object : DetailedPlan.DetailedPlanListener {
            override fun onSkip() {
                // Обработка пропуска приема
            }

            override fun onTake() {
                // Обработка приема лекарства
            }

            override fun onCancel() {
                // Обработка отмены
            }
        })

        dialog.show(fragmentManager, "DetailedPlanDialog")
    }
}