package com.example.organize3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
import com.example.organize3.ui.theme.Organize3Theme
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Organize3Theme {
                Surface(
                    tonalElevation = 5.dp
                ) {
                    val startDestination = remember {
                        mutableStateOf(OrganizeDestination.LoginScreen.route)
                    }
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = getStartDestination()) {
                        composable(route = OrganizeDestination.LoginScreen.route) {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

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
                            var signOutDialogOpened by remember {
                                mutableStateOf(false)
                            }
                            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                            val scope = rememberCoroutineScope()
                            CategoryScreen(
                                onItemSelected = {id ->
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
                                onSignOut = { signOutDialogOpened = true},
                                onArchivedSelected = {
                                    navController.navigateTo(OrganizeDestination.ArchivedScreen.route)
                                },
                                drawerState = drawerState,
                                closeDrawer = {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                                onNavigationIconClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                            if (signOutDialogOpened) {
                                DeleteConfirmationDialog(
                                    onDeleteConfirm = {
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
                                    text = R.string.log_out_message,
                                    actionText = R.string.log_out_cap,
                                    onDeleteCancel = { signOutDialogOpened = false}
                                )
                            }
                        }
                        composable(route = OrganizeDestination.BankAccounts.route) {
                            AddedBankAccountScreen(
                                onAddAccount = {navController.navigateTo(OrganizeDestination.AddBankAccountScreen.route)},
                                onNavigateUp = {navController.navigateUp()},
                                goToBankAccountScreen = {id, isArchived->
                                    navController.navigateTo("${OrganizeDestination.BankAccountDetailsScreen.route}/${id}/${isArchived}")
                                }
                            )
                        }
                        composable(route = OrganizeDestination.ApplicationAccounts.route) {
                            AddedApplicationScreen(
                                onNavigateUp = {navController.navigateUp()},
                                onAddApplication = {navController.navigateTo(OrganizeDestination.AddApplicationAccountScreen.route)},
                                navigateToApplicationAccount = { id, isArchived ->
                                    navController.navigateTo("${OrganizeDestination.ApplicationAccountDetailScreen.route}/${id}/${isArchived}")
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
                            route = OrganizeDestination.ApplicationAccountDetailScreen.route + "/{applicationId}" + "/{isArchived}",
                            arguments = listOf(
                                navArgument("applicationId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                },
                                navArgument("isArchived") {
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
                            route = OrganizeDestination.BankAccountDetailsScreen.route + "/{bankAccountId}" + "/{isArchived}",
                            arguments = listOf(
                                navArgument("bankAccountId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                },
                                navArgument("isArchived") {
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
                                navigateToNote = { folderId, id, isArchived ->
                                    navController.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${id}/${isArchived}")
                                },
                                onAddNote = {folderId, noteId, isArchived->
                                    navController.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${noteId}/${isArchived}")
                                },
                                navigateBack = {navController.popBackStack()}
                            )
                        }
                        composable(
                            route = OrganizeDestination.AddNote.route + "/{folderId}" + "/{noteId}" + "/{isArchived}",
                            arguments = listOf(
                                navArgument("folderId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                },
                                navArgument("noteId") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                },
                                navArgument("isArchived") {
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
                                onCardClick = {folderId, id, isArchived, cardType ->
                                    when (cardType) {
                                        CardType.EmailAccountCard -> navController.navigateTo("${OrganizeDestination.EmailAccountDetailScreen.route}/${id}/${isArchived}")
                                        CardType.ApplicationAccountCard -> navController.navigateTo("${OrganizeDestination.ApplicationAccountDetailScreen.route}/${id}/${isArchived}")
                                        CardType.BankAccountCard -> navController.navigateTo("${OrganizeDestination.BankAccountDetailsScreen.route}/${id}/${isArchived}")
                                        else -> navController.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${id}/${isArchived}")
                                    }
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
    private fun getStartDestination(): String {
            if (googleAuthUiClient.getSignedInUser() != null) {
                return OrganizeDestination.Categories.route
            }
        return OrganizeDestination.LoginScreen.route
    }
}
