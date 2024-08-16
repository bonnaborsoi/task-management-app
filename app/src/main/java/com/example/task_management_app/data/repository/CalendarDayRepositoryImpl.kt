package com.example.task_management_app.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.task_management_app.data.firebase.FirebaseService
import com.example.task_management_app.data.model.Day

class CalendarDayRepositoryImpl(
    private val firebaseService: FirebaseService
) : CalendarDayRepository {

    private val databaseReference = firebaseService.getDatabaseReference("days")

    override suspend fun incrementDay(date: Long): Boolean {
        return try {
            val dayRef = databaseReference.child(date.toString())
            val daySnapshot = dayRef.get().await()

            if (daySnapshot.exists()) {
                val existingDay = daySnapshot.getValue(Day::class.java)
                existingDay?.let {
                    it.quantity += 1
                    dayRef.setValue(it).await()
                }
            } else {
                val newDay = Day(date = date, quantity = 1)
                dayRef.setValue(newDay).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun decrementDay(date: Long): Boolean {
        return try {
            val dayRef = databaseReference.child(date.toString())
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

    override fun getAllDays(): Flow<List<Day>> = flow {
        val snapshot = databaseReference.get().await()
        val days = snapshot.children.mapNotNull { it.getValue(Day::class.java) }
        emit(days)
    }
}