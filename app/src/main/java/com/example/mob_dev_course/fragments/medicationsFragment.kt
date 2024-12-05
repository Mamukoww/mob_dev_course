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

class Fragment3 : Fragment() {

    private lateinit var currentMedsRecyclerView: RecyclerView
    private lateinit var inactiveMedsRecyclerView: RecyclerView
    private lateinit var currentMedsAdapter: MedicationsAdapter
    private lateinit var inactiveMedsAdapter: MedicationsAdapter
    private lateinit var medicationStorage: MedicationStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.medications, container, false)

        medicationStorage = MedicationStorage(requireContext())

        // Найти RecyclerView
        currentMedsRecyclerView = view.findViewById(R.id.currentMedsRecyclerView)
        inactiveMedsRecyclerView = view.findViewById(R.id.inactiveMedsRecyclerView)

        // Инициализация адаптеров
        currentMedsAdapter = MedicationsAdapter(emptyList(), childFragmentManager)
        inactiveMedsAdapter = MedicationsAdapter(emptyList(), childFragmentManager)

        // Установить адаптеры и менеджеры компоновки
        currentMedsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        currentMedsRecyclerView.adapter = currentMedsAdapter

        inactiveMedsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        inactiveMedsRecyclerView.adapter = inactiveMedsAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        updateMedicationLists()
    }

    private fun updateMedicationLists() {
        // Получаем активные медикаменты
        val activeMeds = medicationStorage.getActiveMedications().map {
            MedicationItem(it.name, "${it.comment}")
        }
        currentMedsAdapter.updateData(activeMeds)

        // Получаем неактивные медикаменты
        val inactiveMeds = medicationStorage.getInactiveMedications().map {
            MedicationItem(it.name, "${it.comment}")
        }
        inactiveMedsAdapter.updateData(inactiveMeds)
    }
}