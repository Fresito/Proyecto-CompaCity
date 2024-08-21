package com.example.compacity

data class Computers(
    val brand: String,
    val description: String,
    val model: String,
    val releaseDate: String,
    val serialNumber: String,
    val imageUrl: String? = null
) {
    // Constructor sin argumentos
    constructor() : this("", "", "", "", "","")
}