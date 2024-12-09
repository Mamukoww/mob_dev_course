package com.example.mob_dev_course.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mob_dev_course.R
import com.example.mob_dev_course.adapters.MedicationsAdapter
import com.example.mob_dev_course.adapters.MedicationItem
import com.example.mob_dev_course.data.MedicationStorage
import com.example.mob_dev_course.data.ScheduleStorage

class Fragment3 : Fragment() {

    private lateinit var currentMedsRecyclerView: RecyclerView
    private lateinit var currentMedsAdapter: MedicationsAdapter
    private lateinit var medicationStorage: MedicationStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.medications, container, false)

        medicationStorage = MedicationStorage(requireContext())
        context?.let { ScheduleStorage.initialize(it) }

        // Найти RecyclerView
        currentMedsRecyclerView = view.findViewById(R.id.currentMedsRecyclerView)

        // Инициализация адаптера
        currentMedsAdapter = MedicationsAdapter(
            emptyList(), 
            childFragmentManager, 
            medicationStorage,
            ScheduleStorage // Передаем объект ScheduleStorage
        )

        // Установить адаптер и менеджер компоновки
        currentMedsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        currentMedsRecyclerView.adapter = currentMedsAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        updateMedicationList()
    }

    private fun updateMedicationList() {
        // Получаем активные медикаменты
        val activeMeds = medicationStorage.getActiveMedications().map {
            MedicationItem(
                id = it.id,
                name = it.name,
                description = "${it.type}, ${it.comment}"
            )
        }
        currentMedsAdapter.updateData(activeMeds)
    }
}