@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.example.organize3.applications.home

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
import androidx.compose.material.DismissDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.email.repository.ApplicationAccounts
import com.example.organize3.emailAccounts.home.EmptyPage
import com.example.organize3.ui.theme.shapes
import com.example.organize3.util.RequestState


@Composable
fun AddedApplicationScreen(
    modifier: Modifier = Modifier,
    onNavigateUp:() -> Unit,
    canNavigateBack: Boolean = true,
    navigateWithArgs:(String) -> Unit,
    archiveApplication: (ApplicationAccount) -> Unit,
    onAddApplication:() -> Unit,
    applicationAccounts: ApplicationAccounts
) {
    Scaffold (
        modifier = modifier.fillMaxSize(),
    floatingActionButton = {
        FloatingActionButton(
            onClick = onAddApplication,
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) {
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
        when (applicationAccounts) {
            is RequestState.Success -> {
                ApplicationScreen(
                    Modifier.padding(values),
                    applicationList = applicationAccounts.data,
                    onApplicationClick = navigateWithArgs,
                    archiveApplication = archiveApplication,
                )
            }
            is RequestState.Error -> {
                EmptyPage(
                    title = "Error",
                    subtitle = "${applicationAccounts.error.message}"
                )
            }
            is RequestState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else -> {

            }
        }

    }
}

@Composable
fun ApplicationScreen(
    modifier: Modifier = Modifier,
    applicationList: List<ApplicationAccount>,
    onApplicationClick: (String) -> Unit,
    archiveApplication: (ApplicationAccount) -> Unit,
) {

            ApplicationList(
                modifier = modifier,
                applicationList = applicationList,
                onApplicationClick = onApplicationClick,
                archiveApplication = archiveApplication
            )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ApplicationList(
    modifier: Modifier = Modifier,
    applicationList: List<ApplicationAccount>,
    onApplicationClick: (String) -> Unit,
    archiveApplication: (ApplicationAccount) -> Unit,
) {
    var isHintDisplayed by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
//        SearchField(
//            value = searchQuery,
//            onValueChanged = onValueChanged,
//            isHintDisplayed = isHintDisplayed,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(100))
//                .background(MaterialTheme.colorScheme.surfaceVariant)
//                .onFocusChanged {
//                    isHintDisplayed = !(it.hasFocus)
//                },
//            hintText = stringResource(id = R.string.search_application_account))
//        Spacer(modifier = Modifier.height(16.dp))
        if (applicationList.isNotEmpty()) {
            LazyColumn(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = applicationList, key = {it._id.toString()}) {application ->
                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {

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
                                    androidx.compose.material.DismissValue.Default -> MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                                    else -> Color.Green
                                }
                            )
                            val alignment = Alignment.CenterEnd
                            val icon = R.drawable.ic_archived

                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == androidx.compose.material.DismissValue.Default) 0.75f else 1f
                            )
                            Box (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = Dp(20f))
                                ,
                                contentAlignment = alignment
                            ){
                                Image(
                                    painter = painterResource(id = icon),
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
                                shape = shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(alignment = Alignment.CenterVertically),
                                backgroundColor  = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                ApplicationCard(
                                    application = application,
                                    onApplicationClick = onApplicationClick)
                            }
                        }
                    )

                }
            }
        } else {
            EmptyPage(
                title = stringResource(id = R.string.empty_application_account)
            )
        }
    }
}

@Composable
fun ApplicationCard(
    modifier: Modifier = Modifier,
    onApplicationClick: (String) -> Unit,
    application: ApplicationAccount
) {
    Card(modifier = modifier
        .padding(4.dp)
        .clickable { onApplicationClick(application._id.toHexString()) }
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
                painter = painterResource(id = R.drawable.website_logo),
                contentDescription = stringResource(id = R.string.appLogo),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = application.title,
                modifier = Modifier
                    .padding(8.dp),
                style = MaterialTheme.typography.titleLarge
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