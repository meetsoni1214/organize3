package com.example.organize3

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.organize3.applications.UserPreferencesRepository
import com.example.organize3.data.AppContainer
import com.example.organize3.data.AppDataContainer

private const val IS_SOCIALS_PREFERENCE_NAME = "socials_preferences"
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = IS_SOCIALS_PREFERENCE_NAME
)
class OrganizeApp: Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(datastore)
    }

}