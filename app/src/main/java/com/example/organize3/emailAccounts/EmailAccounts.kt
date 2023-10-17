@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class
)

package com.example.organize3.emailAccounts

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.archived.CardType
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.ui.theme.shapes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddedEmailAccountsScreen(
    navigateToEmailAccount: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp:() -> Unit,
    onAddEmail:() -> Unit,
    viewModel: EmailHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val emailHomeUiState by viewModel.emailHomeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val searchQuery by viewModel.searchText.collectAsState()
    val emailAccounts by viewModel.emailAccounts.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEmail,
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.email_category),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }){ values ->
        EmailScreen(
            modifier = modifier.padding(values),
            emailList = emailAccounts,
            realEmailList = emailHomeUiState.emailList,
            onEmailClick = navigateToEmailAccount,
            onValueChanged = viewModel::onSearchTextChange,
            searchQuery = searchQuery,
            deleteEmail = { emailAccount ->
                coroutineScope.launch {
                    viewModel.archiveEmail(emailAccount)
//                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
//                        message = "Item moved to Archived!",
//                        actionLabel = "Undo"
//                    )
//                    when (snackbarResult) {
//                        SnackbarResult.Dismissed -> Log.d("SnackbarDemo", "Dismissed")
//                        SnackbarResult.ActionPerformed -> {
//                            viewModel.undoArchiveEmail(emailAccount)
//                        }
//                    }
                }
            },
            isSearching = isSearching
        )
    }
}

@Composable
fun EmailScreen(
    modifier: Modifier = Modifier,
    emailList: List<EmailAccount>,
    realEmailList: List<EmailAccount>,
    isSearching: Boolean,
    searchQuery: String,
    onValueChanged: (String) -> Unit,
    onEmailClick: (Int, Int) -> Unit,
    deleteEmail: (EmailAccount) -> Unit
) {
    EmailList(
        modifier = modifier,
        onEmailClick = {onEmailClick(it.id, 0)},
        searchQuery = searchQuery,
        onValueChanged = onValueChanged,
        emailList = emailList,
        deleteEmail = deleteEmail,
        isSearching = isSearching,
        realEmailList = realEmailList
    )
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmailList(
    modifier: Modifier = Modifier,
    searchQuery: String,
    isSearching: Boolean,
    onValueChanged: (String) -> Unit,
    realEmailList: List<EmailAccount>,
    onEmailClick: (EmailAccount) -> Unit,
    emailList: List<EmailAccount>,
    deleteEmail: (EmailAccount) -> Unit
) {
    var isHintDisplayed by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
       SearchField(
           value = searchQuery,
           isHintDisplayed = isHintDisplayed,
           modifier = Modifier
               .fillMaxWidth()
               .clip(RoundedCornerShape(100))
               .background(MaterialTheme.colorScheme.surfaceVariant)
               .onFocusChanged {
                   isHintDisplayed = !(it.hasFocus)
               }
           ,
           hintText = stringResource(id = R.string.hint_email_search),
           onValueChanged = onValueChanged)
        Spacer(modifier = Modifier.height(16.dp))
        if (emailList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (realEmailList.isEmpty()) {
                    Text(text = stringResource(id = R.string.category_screen_text_email),
                        modifier = Modifier.padding(horizontal = 16.dp))
                }else {
                    Text(text = stringResource(id = R.string.no_search_result_found),
                        modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }else {
                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items = emailList, key = { it.id}) { email ->
                        val dismissState = rememberDismissState()

                        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                            deleteEmail(email)
                        }
                        SwipeToDismiss(
                            state = dismissState,
                            modifier = Modifier
                                .padding(vertical = Dp(1f)),
                            directions = setOf(
                                DismissDirection.EndToStart
                            ),
                            dismissThresholds = { direction ->
                                androidx.compose.material.FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.3f else 0.05f)
                            },
                            background = {
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                                        else -> Color.Green
                                    },
                                )
                                val alignment = Alignment.CenterEnd
                                val icon = R.drawable.ic_archived

                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                                )
                                Box (
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = Dp(20f))
                                    ,
                                    contentAlignment = alignment
                                ){
                                    Image(painter = painterResource(id = icon),
                                        contentDescription = "Archive Icon",
                                        modifier = Modifier.scale(scale)
                                    )
                                }
                            },
                            dismissContent = {
                                Card(
                                    elevation = animateDpAsState(
                                        if (dismissState.dismissDirection != null) 4.dp else 0.dp
                                    ).value,
                                    backgroundColor  = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = shapes.medium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(alignment = Alignment.CenterVertically)
                                ) {
                                    EmailAccountCard(
                                        email = email,
                                        onEmailClick = onEmailClick
                                    )
                                }
                            }
                        )
                    }
                }
        }
    }
}

@Composable
public fun SearchField(
    value: String,
    onValueChanged: (String) -> Unit,
    isHintDisplayed: Boolean,
    modifier: Modifier = Modifier,
    hintText: String = "",
    textStyle: TextStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = TextUnit.Unspecified
    ),
    maxLines: Int = 1
) {
   BasicTextField(
       value = value,
       onValueChange = onValueChanged,
       textStyle = textStyle,
       maxLines = maxLines,
       cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
       decorationBox = { innerTextField ->
           Box(
               modifier = modifier
                   .padding(12.dp)
           ) {
               if (value.isEmpty() && isHintDisplayed) {
                   Row(
                       modifier = Modifier.fillMaxWidth(),
                       verticalAlignment = Alignment.CenterVertically
                   ) {
                       Icon(
                           imageVector = Icons.Default.Search,
                           tint = MaterialTheme.colorScheme.onSurfaceVariant,
                           contentDescription = stringResource(id = R.string.search))
                       Spacer(modifier = Modifier.width(12.dp))
                       Text(
                           text = hintText,
                           color = MaterialTheme.colorScheme.onSurfaceVariant,
                       )
                   }
               }
               innerTextField()
           }
       }
   )
}

@Composable
fun EmailAccountCard(
    email: EmailAccount,
    onEmailClick: (EmailAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier
        .padding(4.dp)
        .clickable { onEmailClick(email) }
        .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.email_icon),
                contentDescription = stringResource(id = R.string.email_icon),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = email.accountTitle,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailCardPreview() {
    EmailAccountCard(
        email = EmailAccount(
            accountEmail = "meetsoni1214@gmail.com",
            accountTitle = "Meet Soni Gmail",
            accountRemarks = "jkda",
            accountPassword = "jkfa"
        ), onEmailClick = {}
    )
}