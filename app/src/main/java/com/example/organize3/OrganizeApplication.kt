@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.organize3

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.organize3.navigation.OrganizeNavHost


@Composable
fun OrganizeApplication(navController: NavHostController = rememberNavController()) {
    OrganizeNavHost(navController = navController)
}

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
    onNavigaationIconClick: () -> Unit = {},
    deleteEmail: () -> Unit = {},
    duplicateEmail: () -> Unit = {},
    saveNote:() -> Unit = {},
    navigateUp: () -> Unit = {},
    onCancelClick:(Boolean) -> Unit = {}
) {
    var showExpandedMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showDialog = rememberSaveable { mutableStateOf(false) }
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
                        DropdownMenuItem(
                            onClick = {
                                    showDialog.value = true
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = { Text(text = stringResource(id = R.string.share))})
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.delete))},
                            onClick = {
                                deleteConfirmationRequired = true
                                showExpandedMenu = !showExpandedMenu
                            })
                        DropdownMenuItem(
                            onClick = {
                                duplicateEmail()
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = {Text(text = stringResource(id = R.string.duplicate))}
                        )
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
                    onDeleteCancel = { deleteConfirmationRequired = false })
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
                        DropdownMenuItem(
                            onClick = {
                                duplicateEmail()
                                showExpandedMenu = !showExpandedMenu
                            },
                            text = {Text(text = stringResource(id = R.string.duplicate_fold))}
                        )
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
                    onClick = onNavigaationIconClick,
                ) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
            }
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
    text: Int = R.string.delete_confirm_email,
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(id = R.string.attention))},
        text = { Text(text = stringResource(id = text))},
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(id = R.string.yes))
            }
        }
    )
}

//@Preview
//@Composable
//fun ShareDialogPreview() {
//    ShareDialog()
//}

