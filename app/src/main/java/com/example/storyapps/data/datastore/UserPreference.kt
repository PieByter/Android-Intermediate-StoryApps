package com.example.storyapps.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
        }
    }

    fun getUser(): Flow<User?> {
        return dataStore.data.map { preferences ->
            User(preferences[KEY_TOKEN])
        }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): UserPreference {
            val dataStore = context.dataStore
            return getInstance(dataStore)
        }
    }

    data class User(
        val token: String?
    )
}
