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

class Fragment3 : Fragment() {

    private lateinit var currentMedsRecyclerView: RecyclerView
    private lateinit var inactiveMedsRecyclerView: RecyclerView
    private lateinit var currentMedsAdapter: MedicationsAdapter
    private lateinit var inactiveMedsAdapter: MedicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.medications, container, false)

        // Найти RecyclerView
        currentMedsRecyclerView = view.findViewById(R.id.currentMedsRecyclerView)
        inactiveMedsRecyclerView = view.findViewById(R.id.inactiveMedsRecyclerView)

        // Инициализация адаптеров
        currentMedsAdapter = MedicationsAdapter(emptyList())
        inactiveMedsAdapter = MedicationsAdapter(emptyList())

        // Установить адаптеры и менеджеры компоновки
        currentMedsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        currentMedsRecyclerView.adapter = currentMedsAdapter

        inactiveMedsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        inactiveMedsRecyclerView.adapter = inactiveMedsAdapter

        // Заполнить тестовыми данными
        populateTestData()

        return view
    }

    private fun populateTestData() {
        val currentMeds = listOf(
            MedicationItem("Лекарство A", "Описание текущего лекарства"),
            MedicationItem("Лекарство B", "Описание текущего лекарства")
        )
        val inactiveMeds = listOf(
            MedicationItem("Лекарство C", "Описание неактивного лекарства"),
            MedicationItem("Лекарство D", "Описание неактивного лекарства")
        )

        currentMedsAdapter.updateData(currentMeds)
        inactiveMedsAdapter.updateData(inactiveMeds)
    }
}
