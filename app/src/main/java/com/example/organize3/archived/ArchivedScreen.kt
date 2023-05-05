package com.example.organize3.archived

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.data.email.EmailAccount

@Composable
fun ArchivedScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    onCardClick: (Int, Int) -> Unit,
    viewModel: ArchivedHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val archivedHomeUiState by viewModel.archivedHomeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.archived_details),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { values ->
        ArchivedScreenBody(
            modifier = modifier.padding(values),
            emailList = archivedHomeUiState.emailList,
            onCardClick = onCardClick
        )
    }
}

@Composable
fun ArchivedScreenBody(
    modifier: Modifier = Modifier,
    emailList: List<EmailAccount>,
    onCardClick: (Int, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = emailList, key = { it.id}) {email ->
            ArchivedCard(
                imageRes = R.drawable.email_icon,
                contentDes = stringResource(id = R.string.email_icon),
                title = email.accountTitle,
                onCardClick = onCardClick,
                cardId = email.id
            )
        }
    }
}

@Composable
fun ArchivedCard(
    modifier: Modifier = Modifier,
    imageRes: Int,
    contentDes: String,
    onCardClick: (Int, Int) -> Unit,
    cardId: Int,
    title: String
) {
    Card (
        modifier = modifier
            .padding(4.dp)
            .clickable {
                onCardClick(cardId, 1)
            }
            .fillMaxWidth()
            ){
        Row(modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = contentDes,
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}