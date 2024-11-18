package com.example.mob_dev_course.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R

data class MedicationItem(val name: String, val description: String)

class MedicationsAdapter(private var data: List<MedicationItem>) :
    RecyclerView.Adapter<MedicationsAdapter.MedicationViewHolder>() {

    class MedicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.medicationName)
        val descriptionTextView: TextView = view.findViewById(R.id.medicationDescription)
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
}
