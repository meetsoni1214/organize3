package com.example.organize3.applications

import android.util.Log
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val datastore: DataStore<Preferences>
) {
    private companion object {
        val IS_SOCIALS =  booleanPreferencesKey("is_socials")
        const val TAG = "UserPreferencesRepo"
    }
    val isSocials: Flow<Boolean> = datastore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading Preferences", it)
                emit(emptyPreferences())
            }else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_SOCIALS] ?: true
        }
    suspend fun saveSocialsPreference(isSocials: Boolean) {
        datastore.edit { preferences ->
            preferences[IS_SOCIALS] = isSocials
        }
    }
}