package com.example.organize3.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.application.ApplicationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * View Model to retrieve all items in the Room database.
 */
class ApplicationViewModel(
    private val applicationRepository: ApplicationRepository,
    private val userPreferencesRepository: UserPreferencesRepository): ViewModel(){
    val applicationHomeUiState: StateFlow<ApplicationHomeUiState> = applicationRepository.getAllApplicationsStream().map {
        ApplicationHomeUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ApplicationHomeUiState()
    )
    val anotherUiState: StateFlow<AnotherUiState> =
        userPreferencesRepository.isSocials.map { isSocials ->
        AnotherUiState(isSocials)
    }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AnotherUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun selectSocials(isSocials: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveSocialsPreference(isSocials)
        }
    }
    private fun saveSocials(list: List<ApplicationAccount>) {
        viewModelScope.launch {
            applicationRepository.insertApplications(list)
        }
    }

    fun addAllSocials() {
            val socials = listOf<ApplicationAccount>(
                ApplicationAccount(accountTitle = "Meet Soni(Sample)", accountUsername = "meetsoni1214@gmail.com", accountPassword = "fb_meet", accountRemarks = "Personal Facebook Account")
            )
            saveSocials(socials)
            selectSocials(false)
    }
    suspend fun archiveApplication(applicationAccount: ApplicationAccount) {
        applicationRepository.updateApplication(applicationAccount.copy(isArchived = 1))
    }
    suspend fun undoArchiveApplication(applicationAccount: ApplicationAccount) {
        applicationRepository.updateApplication(applicationAccount.copy(isArchived = 0))
    }
}
/**
 * Ui State for ApplicationHomeScreen
 */
data class ApplicationHomeUiState(val applicationList: List<ApplicationAccount> = listOf())
data class AnotherUiState(val isSocials: Boolean = true)