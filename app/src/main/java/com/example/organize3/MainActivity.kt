package com.example.organize3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.organize3.applications.AddApplicationScreen
import com.example.organize3.applications.AddedApplicationScreen
import com.example.organize3.applications.ApplicationDetailScreen
import com.example.organize3.applications.ApplicationEditScreen
import com.example.organize3.archived.ArchivedScreen
import com.example.organize3.archived.CardType
import com.example.organize3.bankAccounts.*
import com.example.organize3.emailAccounts.AddEmailAccountScreen
import com.example.organize3.emailAccounts.AddedEmailAccountsScreen
import com.example.organize3.emailAccounts.EmailDetailScreen
import com.example.organize3.emailAccounts.EmailEditScreen
import com.example.organize3.navigation.OrganizeDestination
import com.example.organize3.notes.AddNoteScreen
import com.example.organize3.notes.NotesHome
import com.example.organize3.presentation.sign_in.GoogleAuthUiClient
import com.example.organize3.presentation.sign_in.SignInViewModel
import com.example.organize3.splashScreen.SplashScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

//    private val googleAuthUiClient by lazy {
//        GoogleAuthUiClient(
//            context = applicationContext,
//            oneTapClient = Identity.getSignInClient(applicationContext)
//        )
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appState = rememberAppState()
                    androidx.compose.material.Scaffold(
                        snackbarHost = {
                            androidx.compose.material.SnackbarHost(
                                hostState = it,
                                modifier = Modifier.padding(8.dp),
                                snackbar = { snackbarData ->
                                    androidx.compose.material.Snackbar(snackbarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                                }
                            )
                        },
                        scaffoldState = appState.scaffoldState
                    ){ innerPaddingModifier ->
                        NavHost(
                            navController = appState.navController,
                            startDestination = OrganizeDestination.SplashScreen.route,
                            modifier = Modifier.padding(innerPaddingModifier)
                        ) {
                            organizeNavGraph(appState)
                        }
                    }

                }
            }
        }
    }
    private fun NavHostController.navigateTo(route: String) {
        this.navigate(route)
    }
    private fun NavHostController.navigateAndPopUp(route: String, popUp: String) {
        this.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) {inclusive = true}
        }
    }
}
