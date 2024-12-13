package com.example.mob_dev_course.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.graphics.Color
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.fragments.DetailedPlan
import com.example.mob_dev_course.data.MedicationStatus
import com.example.mob_dev_course.data.ScheduleStorage

data class ScheduleItem(
    val id: String,
    val time: String,
    val title: String,
    val description: String,
    var status: MedicationStatus
)


class ScheduleAdapter(
    private var data: List<ScheduleItem>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.scheduleTime)
        val titleText: TextView = view.findViewById(R.id.scheduleTitle)
        val descriptionText: TextView = view.findViewById(R.id.scheduleDescription)
        private val statusIcon: ImageView = view.findViewById(R.id.statusIcon)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDetailedPlan(data[position])
                }
            }
        }

        fun updateStatus(status: MedicationStatus) {
            val iconResource = when (status) {
                MedicationStatus.PENDING -> R.drawable.logo2
                MedicationStatus.TAKEN -> R.drawable.ic_check
                MedicationStatus.SKIPPED -> R.drawable.ic_arrow_right
                MedicationStatus.CANCELLED -> R.drawable.ic_close
            }
            statusIcon.setImageResource(iconResource)
            if (status == MedicationStatus.PENDING) {
                statusIcon.clearColorFilter()
            } else {
                statusIcon.setColorFilter(Color.WHITE)
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
        holder.updateStatus(item.status)
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
            override fun onTake() {
                updateItemStatus(item.id, MedicationStatus.TAKEN)
            }

            override fun onSkip() {
                updateItemStatus(item.id, MedicationStatus.SKIPPED)
            }

            override fun onCancel() {
                updateItemStatus(item.id, MedicationStatus.PENDING)
            }
        })

        dialog.show(fragmentManager, "DetailedPlanDialog")
    }

    private fun updateItemStatus(itemId: String, newStatus: MedicationStatus) {
        // Обновляем статус в хранилище
        ScheduleStorage.updateScheduleStatus(itemId, newStatus)
        
        // Обновляем статус в локальных данных
        val index = data.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val updatedList = data.toMutableList()
            updatedList[index] = updatedList[index].copy(status = newStatus)
            updateData(updatedList)
        }
    }
}