package com.example.organize3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.organize3.applications.add_update.AddApplicationScreen
import com.example.organize3.applications.home.AddedApplicationScreen
import com.example.organize3.applications.details.ApplicationDetailScreen
import com.example.organize3.applications.add_update.ApplicationEditViewModel
import com.example.organize3.applications.details.ApplicationDetailViewModel
import com.example.organize3.applications.home.ApplicationViewModel
import com.example.organize3.archived.ArchivedScreen
import com.example.organize3.archived.CardType
import com.example.organize3.bankAccounts.*
import com.example.organize3.emailAccounts.add_update.AddEmailAccountScreen
import com.example.organize3.emailAccounts.home.AddedEmailAccountsScreen
import com.example.organize3.emailAccounts.details.EmailDetailScreen
import com.example.organize3.emailAccounts.details.EmailDetailViewModel
import com.example.organize3.emailAccounts.home.EmailViewModel
import com.example.organize3.emailAccounts.add_update.WriteEmailViewModel
import com.example.organize3.navigation.OrganizeDestination
import com.example.organize3.notes.AddNoteScreen
import com.example.organize3.notes.NotesHome
import com.example.organize3.presentation.sign_in.GoogleAuthUiClient
import com.example.organize3.presentation.sign_in.SignInViewModel
import com.example.organize3.ui.theme.Organize3Theme
import com.example.organize3.util.Constants.APPLICATION_SCREEN_ARGUMENT_KEY
import com.example.organize3.util.Constants.EMAIL_SCREEN_ARGUMENT_KEY
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
                                                intent = result.data ?: return@launch,
                                                onSuccessfulFirebaseSignIn = { tokenId ->
                                                    viewModel.signInWithMongAtlas(
                                                        tokenId = tokenId,
                                                    )
                                                }
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
                            val viewModel: ApplicationViewModel = viewModel()
                            val applicationAccounts by viewModel.applicationAccounts
                            val context = LocalContext.current
                            AddedApplicationScreen(
                                onNavigateUp = {navController.navigateUp()},
                                onAddApplication = {navController.navigateTo(OrganizeDestination.AddApplicationAccountScreen.route)},
                                navigateWithArgs = {
                                    navController.navigateTo(OrganizeDestination.ApplicationAccountDetailScreen.passId(appId = it))
                                },
                                applicationAccounts = applicationAccounts,
                                archiveApplication = { applicationAccount ->
                                    viewModel.archiveApplication(
                                        applicationAccount = applicationAccount,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Application Account Archived!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            )
                        }
                        composable(route = OrganizeDestination.EmailAccounts.route) {
                            val viewModel: EmailViewModel = viewModel()
                            val emailAccounts by viewModel.emailAccounts
                            val context = LocalContext.current
                            val searchQuery by viewModel.searchText.collectAsState()
                            val isSearching by viewModel.isSearching.collectAsState()
                            AddedEmailAccountsScreen(
                                onAddEmail = {navController.navigateTo(OrganizeDestination.AddEmailAccountScreen.route)},
                                onNavigateUp = {navController.navigateUp()},
                                emailAccounts = emailAccounts,
                                navigateWithArgs = {
                                    navController.navigateTo(OrganizeDestination.EmailAccountDetailScreen.passId(emailId = it))
                                },
                                searchQuery = searchQuery,
                                onValueChanged = viewModel::onSearchTextChange,
                                archiveEmail = {
                                        emailAccount ->

                                    viewModel.archiveEmail(
                                        emailAccount = emailAccount,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Email Account Archived!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
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
                        composable(
                            route = OrganizeDestination.AddEmailAccountScreen.route,
                            arguments = listOf(navArgument(name = EMAIL_SCREEN_ARGUMENT_KEY) {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) {
                            val context = LocalContext.current
                            val viewModel: WriteEmailViewModel = viewModel()
                            val uiState = viewModel.uiState
                            AddEmailAccountScreen(
                                uiState = uiState,
                                onNavigateUp = {navController.navigateUp()},
                                navigateBack = {navController.popBackStack()},
                                onTitleChanged = {viewModel.setTitle(title = it)},
                                onEmailChanged = { viewModel.setEmail(email = it)},
                                onPasswordChanged = {viewModel.setPassword(password = it)},
                                onRemarksChanged = { viewModel.setRemarks(remarks = it)},
                                onSaveClick = {
                                    viewModel.upsertEmailAccount(
                                        emailAccount = it,
                                        onSuccess = { navController.popBackStack() },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                            )
                        }
                        composable(
                            route = OrganizeDestination.EmailAccountDetailScreen.route,
                            arguments = listOf(navArgument(name = EMAIL_SCREEN_ARGUMENT_KEY) {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ){
                            val context = LocalContext.current
                            val viewModel: EmailDetailViewModel = viewModel()
                            val uiState = viewModel.uiState
                            EmailDetailScreen(
                                uiState = uiState,
                                navigateBack = {navController.navigateUp()},
                                goToEditScreen = {
                                    navController.navigateTo(OrganizeDestination.AddEmailAccountScreen.passId(emailId = it))
                                },
                                onDuplicate = {
                                    viewModel.duplicateEmailAccount(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Copy Created Successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                                onArchive = {
                                    viewModel.archiveEmailAccount(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Email Account Archived!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                                onDeleteConfirmed = {
                                    viewModel.deleteEmailAccount(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Deleted Email Account Successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            )
                        }
                        composable(
                            route = OrganizeDestination.AddApplicationAccountScreen.route,
                            arguments = listOf(navArgument(name = APPLICATION_SCREEN_ARGUMENT_KEY){
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) {
                            val viewModel: ApplicationEditViewModel = viewModel()
                            val uiState = viewModel.uiState
                            val context = LocalContext.current
                            AddApplicationScreen(
                                uiState = uiState,
                                onTitleChanged = {viewModel.setTitle(it)},
                                onPasswordChanged = {viewModel.setPassword(it)},
                                onRemarksChanged = {viewModel.setRemarks(it)},
                                onUsernameChanged = {viewModel.setUsername(it)},
                                onSaveClick = {
                                    viewModel.upsertApplicationAccount(
                                        applicationAccount = it,
                                        onSuccess = { navController.popBackStack() },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.d("ERROR", message)
                                        }
                                    )
                                },
                                navigateUp = { navController.navigateUp() },
                                navigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = OrganizeDestination.ApplicationAccountDetailScreen.route,
                            arguments = listOf(
                                navArgument(APPLICATION_SCREEN_ARGUMENT_KEY) {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) {
                            val viewModel: ApplicationDetailViewModel = viewModel()
                            val uiState = viewModel.uiState
                            val context = LocalContext.current
                            ApplicationDetailScreen(
                                uiState = uiState,
                                onDeleteConfirmed = {
                                    viewModel.deleteApplicationAccount(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Deleted Application Account Successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                                onArchive = {
                                            viewModel.archiveApplicationAccount(
                                                onSuccess = {
                                                    Toast.makeText(
                                                        context,
                                                        "Application Account Archived!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.popBackStack()
                                                },
                                                onError = { message ->
                                                    Toast.makeText(
                                                        context,
                                                        message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                },
                                onDuplicate = {
                                    viewModel.duplicateApplicationAccount(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Copy Created Successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        },
                                        onError = { message ->
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                                navigateBack = { navController.popBackStack() },
                                gotoEditScreen = { navController.navigateTo(OrganizeDestination.AddApplicationAccountScreen.passId(it))}
                            )
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
