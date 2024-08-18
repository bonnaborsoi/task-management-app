package com.example.task_management_app.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.model.Day
import java.util.Calendar

class CalendarDayRepositoryImpl(
    private val firebaseService: FirebaseService
) : CalendarDayRepository {

    private val databaseReference = firebaseService.getDatabaseReference("days")

    override suspend fun incrementDay(date: Long): Boolean {
        return try {
            val startOfDay = getStartOfDay(date)
            val dayRef = databaseReference.child(startOfDay.toString())
            val daySnapshot = dayRef.get().await()

            if (daySnapshot.exists()) {
                val existingDay = daySnapshot.getValue(Day::class.java)
                existingDay?.let {
                    it.quantity += 1
                    dayRef.setValue(it).await()
                }
            } else {
                val newDay = Day(date = startOfDay, quantity = 1)
                dayRef.setValue(newDay).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun decrementDay(date: Long): Boolean {
        return try {
            val startOfDay = getStartOfDay(date)
            val dayRef = databaseReference.child(startOfDay.toString())
            val daySnapshot = dayRef.get().await()

            if (daySnapshot.exists()) {
                val existingDay = daySnapshot.getValue(Day::class.java)
                existingDay?.let {
                    if (it.quantity > 1) {
                        it.quantity -= 1
                        dayRef.setValue(it).await()
                    } else {
                        dayRef.removeValue().await()
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    // Adicione o método getDayQuantity aqui
    suspend fun getDayQuantity(date: Long): Int? {
        return try {
            val startOfDay = getStartOfDay(date)
            val dayRef = databaseReference.child(startOfDay.toString())
            val daySnapshot = dayRef.get().await()

            if (daySnapshot.exists()) {
                val quantity = daySnapshot.child("quantity").getValue(Int::class.java)
                quantity
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CalendarDayRepositoryImpl", "Failed to get day quantity", e)
            null
        }
    }

    private fun getStartOfDay(dateInMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    override fun getAllDays(): Flow<List<Day>> = flow {
        val snapshot = databaseReference.get().await()
        val days = snapshot.children.mapNotNull { it.getValue(Day::class.java) }
        emit(days)
    }
}