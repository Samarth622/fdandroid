package com.example.foodlens

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val mobile: String,
    val password: String
)

fun getCategoryList(category: String): List<Pair<Int, String>> {

    return when (category) {
        "Beverages" -> listOf(
            Pair(R.drawable.beverages, "Beverages"),
            Pair(R.drawable.beverages, "Beverages"),
            Pair(R.drawable.beverages, "Beverages"),
            Pair(R.drawable.beverages, "Beverages"),
            Pair(R.drawable.beverages, "Beverages"),
            Pair(R.drawable.beverages, "Beverages"),
            Pair(R.drawable.beverages, "Beverages"),

            )
        "Biscuits" -> listOf(
            Pair(R.drawable.biscuits, "Biscuits"),
            Pair(R.drawable.biscuits, "Biscuits"),
            Pair(R.drawable.biscuits, "Biscuits"),
            Pair(R.drawable.biscuits, "Biscuits"),
            Pair(R.drawable.biscuits, "Biscuits"),
            Pair(R.drawable.biscuits, "Biscuits"),
            Pair(R.drawable.biscuits, "Biscuits")
        )
        else -> emptyList() // Return empty list if category is not found
    }
}
