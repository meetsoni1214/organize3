package com.example.organize3.archived


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.bankAccount.BankAccount
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.folderWithNotes.Note

@Composable
fun ArchivedScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    onCardClick: (Int, Int, Int, CardType) -> Unit,
    viewModel: ArchivedHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val emailArchivedHomeUiState by viewModel.emailArchivedState.collectAsState()
    val applicationArchivedHomeUiState by viewModel.applicationArchivedState.collectAsState()
    val bankAccountArchivedHomeUiState by viewModel.bankAccountArchivedState.collectAsState()
    val noteArchivedHomeUiState by viewModel.noteArchivedState.collectAsState()
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
            emailList = emailArchivedHomeUiState.emailList,
            applicationList = applicationArchivedHomeUiState.applicationList,
            bankAccountList = bankAccountArchivedHomeUiState.bankAccountList,
            notesList = noteArchivedHomeUiState.notesList,
            onCardClick = onCardClick
        )
    }
}

@Composable
fun ArchivedScreenBody(
    modifier: Modifier = Modifier,
    emailList: List<EmailAccount>,
    applicationList: List<ApplicationAccount>,
    bankAccountList: List<BankAccount>,
    notesList: List<Note>,
    onCardClick: (Int, Int, Int, CardType) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {



        items(items = applicationList, key = {it.id}) {application ->
            ArchivedCard(
                imageRes = R.drawable.website_logo,
                contentDes = stringResource(id = R.string.appLogo),
                title = application.accountTitle,
                onCardClick = onCardClick,
                cardId = application.id,
                cardType = CardType.ApplicationAccountCard
            )
        }


        items(items = notesList, key = {it.id}) { note ->
            ArchivedNoteCard(
                note = note,
                onCardClick = onCardClick,
                cardType = CardType.NoteCard,
                contentDes = stringResource(id = R.string.notes_icon),
                imageRes = R.drawable.notes
            )
        }

        items(items = emailList, key = { it.id}) {email ->
            ArchivedCard(
                imageRes = R.drawable.email_icon,
                contentDes = stringResource(id = R.string.email_icon),
                title = email.accountTitle,
                onCardClick = onCardClick,
                cardId = email.id,
                cardType = CardType.EmailAccountCard
            )
        }
//        items(items = bankAccountList, key = {it.id}) { bankAccount ->
//            ArchiveBankAccountCard(
//                onCardClick = onCardClick,
//                bankAccount = bankAccount,
//                cardType = CardType.BankAccountCard
//            )
//        }


    }
}

@Composable
fun ArchivedNoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onCardClick: (Int, Int, Int, CardType) -> Unit,
    cardType: CardType,
    contentDes: String,
    imageRes: Int
) {

    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable {
                onCardClick(note.folderId, note.id, note.isArchived, cardType)
            }
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier =  Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notes),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .padding(6.dp),
                    contentDescription = contentDes,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = note.noteTitle,
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = note.noteContent,
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(end = 4.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 5
                    )
                }
            }
        }
    }

}
@Composable
fun ArchiveBankAccountCard(
    modifier: Modifier = Modifier,
    bankAccount: BankAccount,
    onCardClick: (Int, Int, Int, CardType) -> Unit,
    cardType: CardType
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onCardClick(0, bankAccount.id, 1, cardType)
            }
            .padding(4.dp),
        shape = MaterialTheme.shapes.medium,
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = bankAccount.bankLogo),
                contentDescription = stringResource(id = R.string.bank_logo),
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = bankAccount.bankName,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = bankAccount.accountHolderName,
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ArchivedCard(
    modifier: Modifier = Modifier,
    imageRes: Int,
    contentDes: String,
    onCardClick: (Int, Int, Int, CardType) -> Unit,
    cardId: Int,
    title: String,
    cardType: CardType
) {
    Card (
        modifier = modifier
            .padding(4.dp)
            .clickable {
                onCardClick(0, cardId, 1, cardType)
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
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}