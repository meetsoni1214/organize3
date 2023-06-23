@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.organize3

import android.content.Intent
import android.content.res.Resources
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.organize3.applications.AddApplicationScreen
import com.example.organize3.applications.AddedApplicationScreen
import com.example.organize3.applications.ApplicationDetailScreen
import com.example.organize3.applications.ApplicationEditScreen
import com.example.organize3.archived.ArchivedScreen
import com.example.organize3.archived.CardType
import com.example.organize3.bankAccounts.*
import com.example.organize3.data.folderWithNotes.Folder
import com.example.organize3.data.folderWithNotes.FolderWithNotes
import com.example.organize3.emailAccounts.AddEmailAccountScreen
import com.example.organize3.emailAccounts.AddedEmailAccountsScreen
import com.example.organize3.emailAccounts.EmailDetailScreen
import com.example.organize3.emailAccounts.EmailEditScreen
import com.example.organize3.navigation.OrganizeDestination
import com.example.organize3.notes.AddNoteScreen
import com.example.organize3.notes.NotesHome
import com.example.organize3.presentation.sign_in.SignInViewModel
import com.example.organize3.snackbar.SnackbarManager
import com.example.organize3.splashScreen.SplashScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//
//@Composable
//fun OrganizeApplication(navController: NavHostController = rememberNavController()) {
//    OrganizeNavHost(navController = navController)
//}

/**
 * App bar to display title and conditionally display the back navigation.
 */
@Composable
fun OrganizeTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    showMenu: Boolean = false,
    showDone: Boolean = false,
    showFolderMenu: Boolean = false,
    shareSubject: String = "",
    shareText: String = "",
    isBankAccount: Boolean = false,
    isArchived: Boolean = false,
    foldersList: List<FolderWithNotes> = listOf(),
    isBankName:(Boolean) -> Unit = {},
    isAccountHolderName:(Boolean) -> Unit = {},
    isAccountType:(Boolean) -> Unit = {},
    isAccountNo:(Boolean) -> Unit = {},
    isIFSC: (Boolean) -> Unit = {},
    isMobNo:(Boolean) -> Unit = {},
    isEmail:(Boolean) -> Unit = {},
    isRemarks:(Boolean) -> Unit = {},
    isNameOnCard: (Boolean)  -> Unit = {},
    isCardNo:(Boolean) -> Unit = {},
    isExpiryDate:(Boolean) -> Unit = {},
    isCvv: (Boolean)  -> Unit = {},
    isAtmPin:(Boolean) -> Unit = {},
    isUpiPin:(Boolean) -> Unit = {},
    isLoginPin:(Boolean) -> Unit = {},
    isTransactionPin:(Boolean) -> Unit = {},
    onNavigationIconClick: () -> Unit = {},
    deleteEmail: () -> Unit = {},
    duplicateEmail: () -> Unit = {},
    archive: () -> Unit = {},
    unArchive: () -> Unit = {},
    saveNote:() -> Unit = {},
    navigateUp: () -> Unit = {},
    moveNote: (Int) -> Unit = {},
    onCancelClick:(Boolean) -> Unit = {}
) {
    var showExpandedMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val showMoveDialog = rememberSaveable { mutableStateOf(false) }
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    if (canNavigateBack) {
        if (showMenu) {
            TopAppBar(
                title = { Text(text = title)},
                modifier = modifier,
                actions = {
                    IconButton(onClick = { showExpandedMenu = !showExpandedMenu }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "menu_button")
                    }
                    DropdownMenu(
                        expanded = showExpandedMenu,
                        onDismissRequest = { showExpandedMenu = false}
                    ) {
                        if (isArchived) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.unarchived))},
                                onClick = {
                                    unArchive()
                                    showExpandedMenu = !showExpandedMenu
                                })
                        }
                        DropdownMenuItem(
                            onClick = {
                                    showDialog.value = true
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = { Text(text = stringResource(id = R.string.share))})
                        DropdownMenuItem(
                            onClick = {
                                duplicateEmail()
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = {Text(text = stringResource(id = R.string.duplicate))}
                        )
                        if (!isArchived) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.archive))},
                                onClick = {
                                    archive()
                                    showExpandedMenu = !showExpandedMenu
                                })
                        }
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete))},
                            onClick = {
                                deleteConfirmationRequired = true
                                showExpandedMenu = !showExpandedMenu
                            })
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
                    }
                })
            if (deleteConfirmationRequired) {
                    DeleteConfirmationDialog(
                        onDeleteConfirm = deleteEmail,
                        onDeleteCancel = { deleteConfirmationRequired = false},
                        actionText = R.string.my_delete,
                        text = R.string.delete_details_confirm
                    )
            }
            if (showDialog.value) {
                if (isBankAccount) {
                    ShareDialog(
                        onDeleteCancel = {
                            onCancelClick(true)
                            showDialog.value = false},
                        onShareClick = {
                            val intent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(intent, null)
                            context.startActivity(shareIntent)
                            showDialog.value = false
                        },
                        isBankName = isBankName,
                        isAccountHolderName = isAccountHolderName,
                        isAccountType = isAccountType,
                        isAccountNo = isAccountNo,
                        isIFSC = isIFSC,
                        isMobNo = isMobNo,
                        isEmail = isEmail,
                        isRemarks = isRemarks,
                        isNameOnCard = isNameOnCard,
                        isCardNo = isCardNo,
                        isExpiryDate = isExpiryDate,
                        isCvv = isCvv,
                        isUpiPin = isUpiPin,
                        isAtmPin = isAtmPin,
                        isLoginPin = isLoginPin,
                        isTransactionPin = isTransactionPin
                    )
                }else {
                    val intent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, null)
                    context.startActivity(shareIntent)
                    showDialog.value = false
                }

            }
        }else if (showDone){
            TopAppBar(
                title = { Text(text = title)},
                modifier = modifier,
                actions = {
                          IconButton(onClick = {
                              saveNote()
                          }) {
                              Icon(imageVector = Icons.Default.Done, contentDescription = "Save Note")
                          }

                    IconButton(onClick = { showExpandedMenu = !showExpandedMenu }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "menu_button")
                    }
                    DropdownMenu(
                        expanded = showExpandedMenu,
                        onDismissRequest = { showExpandedMenu = false}
                    ) {
                        if (isArchived) {
                            DropdownMenuItem(
                                onClick = {
                                    unArchive()
                                    showExpandedMenu = !showExpandedMenu
                                },
                                text = { Text(text = stringResource(id = R.string.unarchived))})
                        }
                        DropdownMenuItem(
                            onClick = {
                                showDialog.value = true
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = { Text(text = stringResource(id = R.string.share))})
                        DropdownMenuItem(
                            onClick = {
                                duplicateEmail()
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = {Text(text = stringResource(id = R.string.duplicate))}
                        )
                        if (!isArchived) {
                            DropdownMenuItem(
                                onClick = {
                                    showMoveDialog.value = true
                                    showExpandedMenu = !showExpandedMenu
                                },
                                text = { Text(text = stringResource(id = R.string.move_to))})
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.archive))},
                                onClick = {
                                    archive()
                                    showExpandedMenu = !showExpandedMenu
                                })
                        }
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete))},
                            onClick = {
                                deleteConfirmationRequired = true
                                showExpandedMenu = !showExpandedMenu
                            })
                    }


                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
                    }
                })
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = deleteEmail,
                    text = R.string.delete_details_confirm,
                    actionText = R.string.my_delete,
                    onDeleteCancel = { deleteConfirmationRequired = false })
            }
            if (showDialog.value) {
                val intent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, null)
                context.startActivity(shareIntent)
                showDialog.value = false
            }

            if (showMoveDialog.value) {
                MoveDialog(
                    foldersList = foldersList,
                    onDeleteCancel = {
                        showMoveDialog.value = false
                    },
                    moveNote = moveNote
                )
            }

        }
        else if (showFolderMenu) {
            TopAppBar(
                title = { Text(text = title)},
                modifier = modifier,
                actions = {
                    IconButton(onClick = { showExpandedMenu = !showExpandedMenu }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "menu_button")
                    }
                    DropdownMenu(
                        expanded = showExpandedMenu,
                        onDismissRequest = { showExpandedMenu = false}
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete_fold))},
                            onClick = {
                                deleteConfirmationRequired = true
                                showExpandedMenu = !showExpandedMenu
                            })
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
                    }
                })
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = deleteEmail,
                    actionText = R.string.my_delete,
                    text = R.string.delete_folder_confirm,
                    onDeleteCancel = { deleteConfirmationRequired = false })
            }
                }
        else {
            TopAppBar(
                title = { Text(text = title)},
                modifier = modifier,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
                    }
                })
        }
    }else {
        TopAppBar(
            title = { Text(text = title)},
            modifier = modifier,
            navigationIcon = {
                IconButton(
                    onClick = onNavigationIconClick,
                ) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
            }
        )
    }
}

@Composable
fun MoveDialog(
    modifier: Modifier = Modifier,
    onDeleteCancel: () -> Unit,
    moveNote: (Int) -> Unit,
    foldersList: List<FolderWithNotes>
) {
    Dialog(
        onDismissRequest = {
                           onDeleteCancel()
        },
        content = {
            Surface(
                modifier =
                modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 350.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier =
                Modifier
                    .padding(22.dp)) {
                    Text(
                        text = stringResource(id = R.string.select_dest_folder),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(0.dp, 350.dp)
                            .fillMaxWidth()
                    ) {
                        items(items = foldersList, key = {it.folder.id}) {item ->
                            FolderRow(folder = item.folder, moveNote = moveNote)
                        }
                    }
                }
            }

        }
    )
}

@Composable
fun FolderRow(
    modifier: Modifier = Modifier,
    moveNote: (Int) -> Unit,
    folder: Folder
) {
    Row(
        modifier = modifier
            .clickable { moveNote(folder.id) }
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.folder),
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = folder.folderName,
        )
    }
}

@Composable
fun ShareDialog(
    modifier: Modifier = Modifier,
    onDeleteCancel: () -> Unit,
    onShareClick:() -> Unit,
    isBankName:(Boolean) -> Unit,
    isAccountHolderName:(Boolean) -> Unit,
    isAccountType:(Boolean) -> Unit,
    isAccountNo:(Boolean) -> Unit,
    isIFSC: (Boolean) -> Unit,
    isMobNo:(Boolean) -> Unit,
    isEmail:(Boolean) -> Unit,
    isRemarks:(Boolean) -> Unit,
    isNameOnCard: (Boolean)  -> Unit,
    isCardNo:(Boolean) -> Unit,
    isExpiryDate:(Boolean) -> Unit,
    isCvv: (Boolean)  -> Unit,
    isAtmPin:(Boolean) -> Unit,
    isUpiPin:(Boolean) -> Unit,
    isLoginPin:(Boolean) -> Unit,
    isTransactionPin:(Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(id = R.string.share_account))},
        text = { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.select_field))}
                CheckBoxes(
                    isBankName = isBankName,
                    isAccountHolderName = isAccountHolderName,
                    isAccountType = isAccountType,
                    isAccountNo = isAccountNo,
                    isIFSC = isIFSC,
                    isMobNo = isMobNo,
                    isEmail = isEmail,
                    isRemarks = isRemarks,
                    isNameOnCard = isNameOnCard,
                    isCardNo = isCardNo,
                    isExpiryDate = isExpiryDate,
                    isCvv = isCvv,
                    isAtmPin = isAtmPin,
                    isLoginPin = isLoginPin,
                    isTransactionPin = isTransactionPin,
                    isUpiPin = isUpiPin
                )
            },
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onShareClick) {
                Text(text = stringResource(id = R.string.share_cap))
            }
        }
    ) 
}

@Composable
fun LabelledCheckbox(
    modifier: Modifier = Modifier,
    isSelected:(Boolean) -> Unit,
    text: String
) {
    val isChecked = rememberSaveable { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = it
                isSelected(isChecked.value)
                              },
            enabled = true,
        )
        Text(text = text)
    }
}

@Composable
fun CheckBoxes(
    modifier: Modifier = Modifier,
    isBankName:(Boolean) -> Unit,
    isAccountHolderName:(Boolean) -> Unit,
    isAccountType:(Boolean) -> Unit,
    isAccountNo:(Boolean) -> Unit,
    isIFSC: (Boolean) -> Unit,
    isMobNo:(Boolean) -> Unit,
    isEmail:(Boolean) -> Unit,
    isRemarks:(Boolean) -> Unit,
    isNameOnCard: (Boolean)  -> Unit,
    isCardNo:(Boolean) -> Unit,
    isExpiryDate:(Boolean) -> Unit,
    isCvv: (Boolean)  -> Unit,
    isAtmPin:(Boolean) -> Unit,
    isUpiPin:(Boolean) -> Unit,
    isLoginPin:(Boolean) -> Unit,
    isTransactionPin:(Boolean) -> Unit,
    ) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        LabelledCheckbox(text = stringResource(id = R.string.bank_name), isSelected = isBankName)
        LabelledCheckbox(text = stringResource(id = R.string.account_holder_name), isSelected = isAccountHolderName)
        LabelledCheckbox(text = stringResource(id = R.string.account_type), isSelected = isAccountType)
        LabelledCheckbox(text = stringResource(id = R.string.account_no), isSelected = isAccountNo)
        LabelledCheckbox(text = stringResource(id = R.string.account_ifsc), isSelected = isIFSC)
        LabelledCheckbox(text = stringResource(id = R.string.account_mob_no), isSelected = isMobNo)
        LabelledCheckbox(text = stringResource(id = R.string.account_email), isSelected = isEmail)
        LabelledCheckbox(text = stringResource(id = R.string.remarks), isSelected = isRemarks)
        LabelledCheckbox(text = stringResource(id = R.string.name_on_card), isSelected = isNameOnCard)
        LabelledCheckbox(text = stringResource(id = R.string.card_no), isSelected = isCardNo)
        LabelledCheckbox(text = stringResource(id = R.string.expiry_date), isSelected = isExpiryDate)
        LabelledCheckbox(text = stringResource(id = R.string.cvv), isSelected = isCvv)
        LabelledCheckbox(text = stringResource(id = R.string.atm_pin), isSelected = isAtmPin)
        LabelledCheckbox(text = stringResource(id = R.string.upi_pin), isSelected = isUpiPin)
        LabelledCheckbox(text = stringResource(id = R.string.login_pin), isSelected = isLoginPin)
        LabelledCheckbox(text = stringResource(id = R.string.transaction_Pin), isSelected = isTransactionPin)
    }
}

@Composable
fun DeleteConfirmationDialog(
    modifier: Modifier = Modifier,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    actionText: Int = R.string.move,
    text: Int = R.string.archive_confirm_email,
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(id = R.string.attention))},
        text = { Text(text = stringResource(id = text))},
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(id = actionText))
            }
        }
    )
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
        OrganizeAppState(scaffoldState, navController, snackbarManager, resources, coroutineScope)
    }



@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}


fun NavGraphBuilder.organizeNavGraph(appState: OrganizeAppState) {
    composable(route = OrganizeDestination.LoginScreen.route) {
//        val viewModel = hiltViewModel<SignInViewModel>()
//        val state by viewModel.state.collectAsStateWithLifecycle()
//
//                                LaunchedEffect(key1 = Unit) {
//                                    if (googleAuthUiClient.getSignedInUser() != null) {
//                                        navController.navigateTo(OrganizeDestination.Categories.route)
//                                    }
//                                }
//
//                                val launcher = rememberLauncherForActivityResult(
//                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
//                                    onResult = {result ->
//                                        if (result.resultCode == ComponentActivity.RESULT_OK) {
//                                            lifecycleScope.launch {
//                                                val signInResult = googleAuthUiClient.signInWithIntent(
//                                                    intent = result.data ?: return@launch
//                                                )
//                                                viewModel.onSignInResult(signInResult)
//                                            }
//                                        }
//                                    }
//                                )
//
//                                LaunchedEffect(key1 = state.isSignInSuccessful) {
//                                    if (state.isSignInSuccessful) {
//                                        Toast.makeText(
//                                            applicationContext,
//                                            "Sign In Successful",
//                                            Toast.LENGTH_LONG
//                                        ).show()
//
//                                        navController.navigateAndPopUp(OrganizeDestination.Categories.route, OrganizeDestination.LoginScreen.route)
//                                        viewModel.resetState()
//                                    }
//                                }

        LoginScreen(signInEmailPass = { route, popUp ->
                                    appState.navigateAndPopUp(route, popUp)
        },
            goToRegisterScreen = {
                appState.navigateTo(OrganizeDestination.RegisterScreen.route)
            },
            onSignInClick = {
//                                        lifecycleScope.launch {
//                                            val signInIntentSender = googleAuthUiClient.signIn()
//                                            launcher.launch(
//                                                IntentSenderRequest.Builder(
//                                                    signInIntentSender ?: return@launch
//                                                ).build()
//                                            )
//                                        }
            }
        )
    }
    composable(route = OrganizeDestination.SplashScreen.route) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp)})
    }
    composable(route = OrganizeDestination.RegisterScreen.route) {
        RegisterScreen(goToLoginScreen = {appState.navigateTo(OrganizeDestination.LoginScreen.route)})
    }
    // builder parameter will be defined here as the graph
    composable(route = OrganizeDestination.Categories.route) {
        CategoryScreen(onItemSelected = {id ->
            when (id) {
                R.string.bank_category -> appState.navigateTo(OrganizeDestination.BankAccounts.route)
                R.string.application_category -> appState.navigateTo(
                    OrganizeDestination.ApplicationAccounts.route)
                else -> appState.navigateTo(OrganizeDestination.EmailAccounts.route)
            }
        },
            onFolderSelected = {folderId, folderName ->
                appState.navigateTo("${OrganizeDestination.Notes.route}/$folderId/$folderName")
            },
//            userData = googleAuthUiClient.getSignedInUser(),
            onSignOut = {
//                lifecycleScope.launch {
//                    googleAuthUiClient.sighOut()
//                    Toast.makeText(
//                        applicationContext,
//                        "Successfully Signed out!",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    navController.navigateTo(OrganizeDestination.LoginScreen.route)
//                }
            },
            onArchivedSelected = {
                appState.navigateTo(OrganizeDestination.ArchivedScreen.route)
            }
        )
    }
    composable(route = OrganizeDestination.BankAccounts.route) {
        AddedBankAccountScreen(
            onAddAccount = {appState.navigateTo(OrganizeDestination.AddBankAccountScreen.route)},
            onNavigateUp = {appState.navigateUp()},
            goToBankAccountScreen = {id, isArchived->
                appState.navigateTo("${OrganizeDestination.BankAccountDetailsScreen.route}/${id}/${isArchived}")
            }
        )
    }
    composable(route = OrganizeDestination.ApplicationAccounts.route) {
        AddedApplicationScreen(
            onNavigateUp = {appState.navigateUp()},
            onAddApplication = {appState.navigateTo(OrganizeDestination.AddApplicationAccountScreen.route)},
            navigateToApplicationAccount = { id, isArchived ->
                appState.navigateTo("${OrganizeDestination.ApplicationAccountDetailScreen.route}/${id}/${isArchived}")
            }
        )
    }
    composable(route = OrganizeDestination.EmailAccounts.route) {
        AddedEmailAccountsScreen(
            onAddEmail = {appState.navigateTo(OrganizeDestination.AddEmailAccountScreen.route)},
            onNavigateUp = {appState.navigateUp()},
            navigateToEmailAccount = { id, isArchived ->
                appState.navigateTo("${OrganizeDestination.EmailAccountDetailScreen.route}/${id}/${isArchived}" )
            }
        )
    }
    composable(route = OrganizeDestination.AddBankAccountScreen.route) {
        AddBankAccountScreen(
            onNavigateUp = {appState.navigateUp()},
            goToAddOtherDetailScreen = { bName, bLogo->
                val route = OrganizeDestination.AddOtherDetailsScreen.withArgs(bName)
                appState.navigateTo("$route/$bLogo")}
        )
    }
    composable(route = OrganizeDestination.AddEmailAccountScreen.route) {
        AddEmailAccountScreen(
            onNavigateUp = {appState.navigateUp()},
            navigateBack = {appState.popBackStack()}
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
            navigateBack = {appState.navigateUp()},
            goToEditScreen = { appState.navigateTo("${OrganizeDestination.EmailEditScreen.route}/$it")}
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
            navigateBack = {appState.popBackStack()},
            onNavigateUp = {appState.navigateUp()})
    }
    composable(
        route = OrganizeDestination.AddApplicationAccountScreen.route
    ) {
        AddApplicationScreen(
            navigateUp = { appState.navigateUp() },
            navigateBack = { appState.popBackStack() })
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
            navigateBack = { appState.popBackStack() },
            gotoEditScreen = { appState.navigateTo("${OrganizeDestination.ApplicationEditScreen.route}/$it")}
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
            onNavigateUp = { appState.navigateUp() },
            navigateBack = {appState.popBackStack()})
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
            navigateUp = { appState.navigateUp()},
            navigateBack = {
                appState.popBackStackUpto(route = OrganizeDestination.BankAccounts.route, inclusive = false)}
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
                appState.navigateTo(route)
            },
            navigateBack = {appState.popBackStack()})
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
            onNavigateUp = { appState.navigateUp() },
            navigateToNote = { folderId, id, isArchived ->
                appState.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${id}/${isArchived}")
            },
            onAddNote = {folderId, noteId, isArchived->
                appState.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${noteId}/${isArchived}")
            },
            navigateBack = {appState.popBackStack()}
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
            onNavigateUp = { appState.navigateUp() },
            navigateBack = { appState.popBackStack() })
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
            onNavigateUp = { appState.navigateUp()},
            navigateBack = { appState.popBackStack() },
//                goToEditBankScreen = {navController.navigateTo(OrganizeDestination.EditBankScreen.route)}
        )
    }

    composable(
        route = OrganizeDestination.ArchivedScreen.route,

        ) {
        ArchivedScreen(onNavigateUp = {
            appState.navigateUp() },
            onCardClick = {folderId, id, isArchived, cardType ->
                when (cardType) {
                    CardType.EmailAccountCard -> appState.navigateTo("${OrganizeDestination.EmailAccountDetailScreen.route}/${id}/${isArchived}")
                    CardType.ApplicationAccountCard -> appState.navigateTo("${OrganizeDestination.ApplicationAccountDetailScreen.route}/${id}/${isArchived}")
                    CardType.BankAccountCard -> appState.navigateTo("${OrganizeDestination.BankAccountDetailsScreen.route}/${id}/${isArchived}")
                    else -> appState.navigateTo("${OrganizeDestination.AddNote.route}/${folderId}/${id}/${isArchived}")
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

//@Preview
//@Composable
//fun ShareDialogPreview() {
//    ShareDialog()
//}

