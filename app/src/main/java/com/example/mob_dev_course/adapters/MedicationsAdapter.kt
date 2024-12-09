package com.example.mob_dev_course.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.fragments.MedicationDetailsDialog
import com.example.mob_dev_course.data.MedicationStorage
import com.example.mob_dev_course.data.ScheduleStorage

data class MedicationItem(
    val id: String,
    val name: String,
    val description: String
)

class MedicationsAdapter(
    private var data: List<MedicationItem>,
    private val fragmentManager: FragmentManager,
    private val medicationStorage: MedicationStorage,
    private val scheduleStorage: ScheduleStorage
) : RecyclerView.Adapter<MedicationsAdapter.MedicationViewHolder>() {

    inner class MedicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.medicationName)
        val descriptionTextView: TextView = view.findViewById(R.id.medicationDescription)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showMedicationDetails(data[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medication_item, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val item = data[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<MedicationItem>) {
        data = newData
        notifyDataSetChanged()
    }

    private fun showMedicationDetails(item: MedicationItem) {
        val dialog = MedicationDetailsDialog.newInstance(
            title = item.name,
            description = item.description
        )

        dialog.setListener(object : MedicationDetailsDialog.MedicationDetailsListener {
            override fun onEdit() {
                // Обработка редактирования
                Toast.makeText(dialog.context, "Функция редактирования будет добавлена позже", Toast.LENGTH_SHORT).show()
            }

            override fun onDelete() {
                // Удаляем лекарство из хранилища лекарств
                medicationStorage.deleteMedication(item.id)
                
                // Удаляем все расписания для этого лекарства
                scheduleStorage.deleteSchedulesForMedication(item.id)
                
                // Обновляем список
                val updatedData = data.toMutableList()
                updatedData.removeAll { it.id == item.id }
                updateData(updatedData)
                
                Toast.makeText(dialog.context, "Лекарство удалено", Toast.LENGTH_SHORT).show()
            }
        })

        dialog.show(fragmentManager, "MedicationDetailsDialog")
    }
}