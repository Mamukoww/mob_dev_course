package com.example.mob_dev_course.models

data class Drug(
    val name: String = "",
    val composition: String = ""
) {
    // Пустой конструктор необходим для Firebase
    constructor() : this("", "")
}
