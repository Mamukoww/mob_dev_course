package com.example.mob_dev_course.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.fragments.MedicationDetailsDialog

data class MedicationItem(val name: String, val description: String)

class MedicationsAdapter(
    private var data: List<MedicationItem>,
    private val fragmentManager: FragmentManager
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
            }

            override fun onDelete() {
                // Обработка удаления
            }
        })

        dialog.show(fragmentManager, "MedicationDetailsDialog")
    }
}