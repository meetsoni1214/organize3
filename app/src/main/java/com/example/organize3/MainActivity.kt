package com.example.organize3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.organize3.applications.AddApplicationScreen
import com.example.organize3.applications.AddedApplicationScreen
import com.example.organize3.applications.ApplicationDetailScreen
import com.example.organize3.applications.ApplicationEditScreen
import com.example.organize3.archived.ArchivedScreen
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
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = OrganizeDestination.LoginScreen.route) {
                        composable(route = OrganizeDestination.LoginScreen.route) {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigateTo(OrganizeDestination.Categories.route)
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = {result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign In Successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigateTo(OrganizeDestination.Categories.route)
                                    viewModel.resetState()
                                }
                            }

                            LoginScreen(goToMainScreen = {
                                navController.navigateTo(OrganizeDestination.Categories.route)},
                                goToRegisterScreen = {
                                    navController.navigateTo(OrganizeDestination.RegisterScreen.route)
                                },
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable(route = OrganizeDestination.RegisterScreen.route) {
                            RegisterScreen(goToLoginScreen = {navController.navigateTo(OrganizeDestination.LoginScreen.route)})
                        }
                        // builder parameter will be defined here as the graph
                        composable(route = OrganizeDestination.Categories.route) {
                            CategoryScreen(onItemSelected = {id ->
                                when (id) {
                                    R.string.bank_category -> navController.navigateTo(OrganizeDestination.BankAccounts.route)
                                    R.string.application_category -> navController.navigateTo(
                                        OrganizeDestination.ApplicationAccounts.route)
                                    else -> navController.navigateTo(OrganizeDestination.EmailAccounts.route)
                                }
                            },
                                onFolderSelected = {folderId, folderName ->
                                    navController.navigateTo("${OrganizeDestination.Notes.route}/$folderId/$folderName")
                                },
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.sighOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Successfully Signed out!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigateTo(OrganizeDestination.LoginScreen.route)
                                    }
                                },
                                onArchivedSelected = {
                                    navController.navigateTo(OrganizeDestination.ArchivedScreen.route)
                                }
                            )
                        }
                        composable(route = OrganizeDestination.BankAccounts.route) {
                            AddedBankAccountScreen(
                                onAddAccount = {navController.navigateTo(OrganizeDestination.AddBankAccountScreen.route)},
                                onNavigateUp = {navController.navigateUp()},
                                goToBankAccountScreen = {id ->
                                    navController.navigateTo(OrganizeDestination.BankAccountDetailsScreen.withArgs(id))
                                }
                            )
                        }
                        composable(route = OrganizeDestination.ApplicationAccounts.route) {
                            AddedApplicationScreen(
                                onNavigateUp = {navController.navigateUp()},
                                onAddApplication = {navController.navigateTo(OrganizeDestination.AddApplicationAccountScreen.route)},
                                navigateToApplicationAccount = { id ->
                                    navController.navigateTo(OrganizeDestination.ApplicationAccountDetailScreen.withArgs(id))
                                }
                            )
                        }
                        composable(route = OrganizeDestination.EmailAccounts.route) {
                            AddedEmailAccountsScreen(
                                onAddEmail = {navController.navigateTo(OrganizeDestination.AddEmailAccountScreen.route)},
                                onNavigateUp = {navController.navigateUp()},
                                navigateToEmailAccount = { id, isArchived ->
                                    navController.navigateTo("${OrganizeDestination.EmailAccountDetailScreen.route}/${id}/${isArchived}" )
                                }
                            )
                        }
                        composable(route = OrganizeDestination.AddBankAccountScreen.route) {
                            AddBankAccountScreen(
                                onNavigateUp = {navController.navigateUp()},
                                goToAddOtherDetailScreen = { bName, bLogo->
                                    val route = OrganizeDestination.AddOtherDetailsScreen.withArgs(bName)
                                    navController.navigateTo("$route/$bLogo")}
                            )
                        }
                        composable(route = OrganizeDestination.AddEmailAccountScreen.route) {
                            AddEmailAccountScreen(
                                onNavigateUp = {navController.navigateUp()},
                                navigateBack = {navController.popBackStack()}
                            )
                        }
                        composable(
                            route = OrganizeDestination.EmailAccountDetailScreen.route + "/{emailId}" + "/{isArchived}",
                            arguments = listOf(
                                navArgument("emailId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                },
                                navArgument("isArchived") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            EmailDetailScreen(
                                navigateBack = {navController.navigateUp()},
                                goToEditScreen = { navController.navigateTo("${OrganizeDestination.EmailEditScreen.route}/$it")}
                            )
                        }
                        composable(
                            route = OrganizeDestination.EmailEditScreen.route + "/{emailId}",
                            arguments = listOf(
                                navArgument("emailId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            EmailEditScreen(
                                navigateBack = {navController.popBackStack()},
                                onNavigateUp = {navController.navigateUp()})
                        }
                        composable(
                            route = OrganizeDestination.AddApplicationAccountScreen.route
                        ) {
                            AddApplicationScreen(
                                navigateUp = { navController.navigateUp() },
                                navigateBack = { navController.popBackStack() })
                        }
                        composable(
                            route = OrganizeDestination.ApplicationAccountDetailScreen.route + "/{applicationId}",
                            arguments = listOf(
                                navArgument("applicationId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            ApplicationDetailScreen(
                                navigateBack = { navController.popBackStack() },
                                gotoEditScreen = { navController.navigateTo("${OrganizeDestination.ApplicationEditScreen.route}/$it")}
                            )
                        }
                        composable(
                            route = OrganizeDestination.ApplicationEditScreen.route + "/{applicationId}",
                            arguments = listOf(
                                navArgument("applicationId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            ApplicationEditScreen(
                                onNavigateUp = { navController.navigateUp() },
                                navigateBack = {navController.popBackStack()})
                        }
                        composable(
                            route = OrganizeDestination.AddOtherDetailsScreen.route + "/{bName}" + "/{bLogo}",
                            arguments = listOf(
                                navArgument("bName") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                                navArgument("bLogo") {
                                    type = NavType.IntType
                                    defaultValue = R.drawable.bank_image_2
                                }
                            )
                        ) {
                            AddOtherDetailsScreen(
                                navigateUp = { navController.navigateUp()},
                                navigateBack = {
                                    navController.popBackStack(route = OrganizeDestination.BankAccounts.route, inclusive = false)}
                            )
                        }
                        composable(
                            route = OrganizeDestination.BankAccountDetailsScreen.route + "/{bankAccountId}",
                            arguments = listOf(
                                navArgument("bankAccountId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            BankAccountDetailScreen(
                                goToEditScreen = {id ->
                                    val route = OrganizeDestination.BankAccountEditScreen.withArgs(id)
                                    navController.navigateTo(route)
                                },
                                navigateBack = {navController.popBackStack()})
                        }
                        composable(
                            route = OrganizeDestination.Notes.route + "/{id}/{folderName}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.IntType
                                },
                                navArgument("folderName") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            NotesHome(
                                onNavigateUp = { navController.navigateUp() },
                                navigateToNote = { folderId, id ->
                                    navController.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${id}")
                                },
                                onAddNote = {folderId, noteId->
                                    navController.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${noteId}")
                                },
                                navigateBack = {navController.popBackStack()}
                            )
                        }
                        composable(
                            route = OrganizeDestination.AddNote.route + "/{folderId}" + "/{noteId}",
                            arguments = listOf(
                                navArgument("folderId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                },
                                navArgument("noteId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            AddNoteScreen(
                                onNavigateUp = { navController.navigateUp() },
                                navigateBack = { navController.popBackStack() })
                        }

                        composable(
                            route = OrganizeDestination.BankAccountEditScreen.route + "/{bankAccountId}",
                            arguments = listOf(
                                navArgument("bankAccountId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) {
                            BankAccountEditScreen(
                                onNavigateUp = { navController.navigateUp()},
                                navigateBack = { navController.popBackStack() },
//                goToEditBankScreen = {navController.navigateTo(OrganizeDestination.EditBankScreen.route)}
                            )
                        }

                        composable(
                            route = OrganizeDestination.ArchivedScreen.route,

                        ) {
                            ArchivedScreen(onNavigateUp = {
                                navController.navigateUp() },
                                onCardClick = {id, isArchived ->
                                    navController.navigateTo("${OrganizeDestination.EmailAccountDetailScreen.route}/${id}/${isArchived}")
                                }
                            )
                        }
//        composable(
//            route = OrganizeDestination.EditBankScreen.route
//        ) {
//            EditBankScreen(
//                onNavigateUp = { navController.navigateUp() },
//                gotoEditOtherDetailsScreen = {bName, bLogo->
//                    val route = OrganizeDestination.BankAccountEditScreen.withArgs(bName)
//                    navController.navigateTo("$route/$bLogo")}
//            )
//        }
                    }
                }
            }
        }
    }
    private fun NavHostController.navigateTo(route: String) {
        this.navigate(route)
    }
}
