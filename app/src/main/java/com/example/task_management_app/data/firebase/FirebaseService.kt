package com.example.task_management_app.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getDatabaseReference(path: String): DatabaseReference {
        return database.getReference(path)
    }

    suspend fun <T> setValue(reference: DatabaseReference, data: T) {
        reference.setValue(data).await()
    }

    suspend inline fun <reified T> getValue(reference: DatabaseReference): T? {
        val snapshot = reference.get().await()
        return snapshot.getValue(T::class.java)
    }

    suspend fun <T> updateValue(reference: DatabaseReference, data: Map<String, T>) {
        reference.updateChildren(data).await()
    }

    suspend fun deleteValue(reference: DatabaseReference) {
        reference.removeValue().await()
    }
}
