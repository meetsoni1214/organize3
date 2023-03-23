@file:OptIn(ExperimentalMaterialApi::class)

package com.example.organize3.applications

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
import androidx.compose.material.*
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.data.application.ApplicationAccount
import kotlinx.coroutines.launch


@Composable
fun AddedApplicationScreen(
    modifier: Modifier = Modifier,
    onNavigateUp:() -> Unit,
    canNavigateBack: Boolean = true,
    onAddApplication:() -> Unit,
    navigateToApplicationAccount: (Int) -> Unit,
    viewModel: ApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val applicationUiState by viewModel.applicationHomeUiState.collectAsState()
    val anotherUiState by viewModel.anotherUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val isSocials = anotherUiState.isSocials
//    if (isSocials) {
//        viewModel.addAllSocials()
//    }
    val socials = listOf<ApplicationAccount>(
        ApplicationAccount(accountTitle = stringResource(id = R.string.facebook_title),
            accountUsername = stringResource(
            id = R.string.email_id), accountPassword = stringResource(id = R.string.facebook_password), accountRemarks = stringResource(
            id = R.string.facebook_remarks), appLogo = R.drawable.facebook_icon),
        ApplicationAccount(accountTitle = stringResource(id = R.string.facebook_title), accountUsername = stringResource(
            id = R.string.username), accountPassword = stringResource(id = R.string.instagram_password), accountRemarks = stringResource(
            id = R.string.instagram_remarks), appLogo = R.drawable.ig_icon),
        ApplicationAccount(accountTitle = stringResource(id = R.string.facebook_title), accountUsername = stringResource(
            id = R.string.username), accountPassword = stringResource(id = R.string.snapchat_password), accountRemarks = stringResource(
            id = R.string.snapchat_remarks), appLogo = R.drawable.snapchat),
        ApplicationAccount(accountTitle = stringResource(id = R.string.facebook_title), accountUsername = stringResource(
            id = R.string.username), accountPassword = stringResource(id = R.string.twitter_password), accountRemarks = stringResource(
            id = R.string.twitter_remarks), appLogo = R.drawable.twitter_icon),
        ApplicationAccount(accountTitle = stringResource(id = R.string.facebook_title), accountUsername = stringResource(
            id = R.string.email_id), accountPassword = stringResource(id = R.string.linkedin_password), accountRemarks = stringResource(
            id = R.string.linkedin_remarks), appLogo = R.drawable.linkedin_icon),
        )
    Scaffold (
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
    floatingActionButton = {
        FloatingActionButton(onClick = onAddApplication) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    },
    topBar = {
        OrganizeTopAppBar(
            title = stringResource(id = R.string.application_category),
            canNavigateBack = canNavigateBack,
            navigateUp = onNavigateUp
        )
    }){ values ->
        ApplicationScreen(
            Modifier.padding(values),
            applicationList = applicationUiState.applicationList,
            onApplicationClick = navigateToApplicationAccount,
            onShowSnackbar = {
//                    applicationAccount ->
//                coroutineScope.launch {
//
//                }
            }
            ,
            deleteApplication = { applicationAccount ->
                coroutineScope.launch {
                    viewModel.deleteApplication(applicationAccount)

                }
                coroutineScope.launch {
                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Item deleted!",
                        actionLabel = "Undo"
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> Log.d("SnackbarDemo", "Dismissed")
                        SnackbarResult.ActionPerformed -> {
                            viewModel.insertApplication(applicationAccount)
                        }
                    }
                }
            }
//            isSocials =  {
//                if (isSocials) {
//                    coroutineScope.launch {
//                      viewModel.saveSocials(socials)
//                    }
//                    viewModel.selectSocials(isSocials = false)
//                }
//            }
        )
    }
}

@Composable
fun ApplicationScreen(
    modifier: Modifier = Modifier,
    applicationList: List<ApplicationAccount>,
    onApplicationClick: (Int) -> Unit,
    deleteApplication: (ApplicationAccount) -> Unit,
    onShowSnackbar: (ApplicationAccount) -> Unit
//    isSocials: () -> Unit = {}
) {

//    isSocials()
    if (applicationList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.category_screen_text_application),
                modifier = Modifier.padding(horizontal = 16.dp))
        }
    }else {
        Column(modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            ApplicationList(applicationList = applicationList, onApplicationClick = onApplicationClick, deleteApplication = deleteApplication, onShowSnackbar = onShowSnackbar)
        }
    }
}

@Composable
fun ApplicationList(
    modifier: Modifier = Modifier,
    applicationList: List<ApplicationAccount>,
    onApplicationClick: (Int) -> Unit,
    deleteApplication: (ApplicationAccount) -> Unit,
    onShowSnackbar: (ApplicationAccount) -> Unit
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = applicationList, key = {it.id}) {application ->
            val dismissState = rememberDismissState()

            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                deleteApplication(application)
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
                            DismissValue.Default -> Color.White
                            else -> Color.Red
                        }
                    )
                    val alignment = Alignment.CenterEnd
                    val icon = Icons.Default.Delete

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
                        Icon(
                            imageVector = icon,
                            contentDescription = "Delete Icon",
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    Card(
                        elevation = animateDpAsState(
                            if (dismissState.dismissDirection != null) 4.dp else 0.dp
                        ).value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        ApplicationCard(application = application, onApplicationClick = onApplicationClick)
                    }
                }
            )

        }
    }
}

@Composable
fun ApplicationCard(
    modifier: Modifier = Modifier,
    onApplicationClick: (Int) -> Unit,
    application: ApplicationAccount
) {
    Card(modifier = modifier
        .padding(4.dp)
        .clickable { onApplicationClick(application.id) }
        .fillMaxWidth()
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = application.appLogo),
                contentDescription = stringResource(id = R.string.appLogo),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = application.accountTitle,
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

//@Preview
//@Composable
//fun ApplicationAccountPreview() {
//    MaterialTheme {
//        AddedApplicationScreen()
//    }
//}