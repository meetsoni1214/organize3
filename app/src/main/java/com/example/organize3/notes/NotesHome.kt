@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.example.organize3.notes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.data.folderWithNotes.Note
import com.example.organize3.R
import com.example.organize3.emailAccounts.home.SearchField
import com.example.organize3.ui.theme.shapes
import kotlinx.coroutines.launch
import androidx.compose.material.Card as Card1

@Composable
fun NotesHome(
    modifier: Modifier = Modifier,
    onNavigateUp:() -> Unit,
    navigateBack:() -> Unit,
    navigateToNote:(Int, Int, Int) -> Unit,
    onAddNote:(Int, Int, Int) -> Unit,
    viewModel: NotesHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notesHomeUiState by viewModel.noteHomeUiState.collectAsState()
//    val folderUiState by viewModel.folder.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val folderId = viewModel.folderId
    val isSearching by viewModel.isSearching.collectAsState()
    val searchQuery by viewModel.searchText.collectAsState()
    val notes by viewModel.notes.collectAsState()
    var longPressClick by rememberSaveable { mutableStateOf(false) }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onAddNote(viewModel.folderId, -1, 0)
                },
                shape = MaterialTheme.shapes.medium,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            OrganizeTopAppBar(
                title = viewModel.folderName,
                showFolderMenu = true,
                deleteEmail = {
                              coroutineScope.launch {
                                  viewModel.deleteFolder(folderId)
                              }
                    navigateBack()
                },
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
            ){ values ->
        NotesScreen(
            notesList = notes,
            onNoteClick = navigateToNote,
            modifier = modifier.padding(values),
            folderId = viewModel.folderId,
            onLongClick = {value ->
                longPressClick = value
            },
            deleteNote = {note ->
                coroutineScope.launch {
                    viewModel.archiveNote(note)
                }
//                coroutineScope.launch {
//                    val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
//                        message = "Item moved to archive!",
//                        actionLabel = "Undo"
//                    )
//                    when (snackBarResult) {
//                        SnackbarResult.Dismissed -> Log.d("SnackBarDemo", "Dismissed")
//                        SnackbarResult.ActionPerformed -> {
//                        }
//                    }
//                }
            },
            isSearching = isSearching,
            searchQuery = searchQuery,
            realNotesList = notesHomeUiState.notesList.filter { note ->
                note.isArchived == 0
            },
            onValueChange = viewModel::onSearchTextChange
//   This is also true onValueChange = {str -> viewModel.onSearchTextChange(str)}
        )
    }
}

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    notesList: List<Note>,
    onNoteClick: (Int, Int, Int) -> Unit,
    folderId: Int,
    isSearching: Boolean,
    searchQuery: String,
    onValueChange: (String) -> Unit,
    realNotesList: List<Note>,
    onLongClick: (Boolean) -> Unit,
    deleteNote: (Note) -> Unit
    ) {

            NotesList(
                modifier = modifier,
                onNoteClick = { onNoteClick(folderId, it.id, 0) },
                deleteNote = deleteNote,
                notesList = notesList,
                onLongClick = onLongClick,
                isSearching = isSearching,
                onValueChange = onValueChange,
                searchQuery = searchQuery,
                realNotesList = realNotesList
            )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    notesList: List<Note>,
    isSearching: Boolean,
    searchQuery: String,
    onValueChange: (String) -> Unit,
    realNotesList: List<Note>,
    onLongClick: (Boolean) -> Unit,
    deleteNote: (Note) -> Unit
) {
    var multipleSelect by rememberSaveable { mutableStateOf(false) }

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
            searchText = R.string.hint_search_notes,
            onValueChanged = onValueChange)
        Spacer(modifier = Modifier.height(16.dp))
        if (notesList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (realNotesList.isEmpty()) {
                    Text(text = stringResource(id = R.string.no_notes_added_message),
                        modifier = Modifier.padding(horizontal = 16.dp))
                }else {
                    Text(text = stringResource(id = R.string.no_search_result_found),
                        modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = notesList, key = {it.id}) { note ->
                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        deleteNote(note)
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
                                }
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
                            Card1(
                                elevation = animateDpAsState(
                                    if (dismissState.dismissDirection != null) 4.dp else 0.dp
                                ).value,
                                shape = shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(alignment = Alignment.CenterVertically),
                                backgroundColor  = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                NoteCard(onNoteClick = onNoteClick,
                                    note = note,
                                    onLongClick = {value ->
                                        multipleSelect = value
                                    },
                                    multipleSelect = true
                                )
                            }
                        }
                    )

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Boolean) -> Unit,
    multipleSelect: Boolean,
    note: Note
) {
    var longClick by rememberSaveable { mutableStateOf(false) }
    var isSelected by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .padding(4.dp)
            .combinedClickable(
                onClick = {
                    if (longClick) {
                        isSelected = !isSelected
                    } else {
                        onNoteClick(note)
                    }
                },
                onLongClick = {
                    longClick = true
                    onLongClick(true)
                    isSelected = true
                }
            )
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier
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
                    contentDescription = stringResource(id = R.string.notes_icon),
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
                            .padding(top = 4.dp)
                        ,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = note.noteContent,
                        modifier = Modifier
                            .padding(4.dp)
                            .padding(end = 4.dp, bottom = 4.dp)
                        ,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 5
                    )
                }
            }
            if (longClick) {
                if (isSelected) {
                    Image(
                        painter = painterResource(id = R.drawable.outline_circle),
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(12.dp)
                            .size(24.dp),
                        contentDescription = null)
                }else {
                    Icon(
                        modifier = Modifier
                            .padding(12.dp),
                        painter = painterResource(id = R.drawable.check_circle),
                        contentDescription = null)
                }
            }
        }
    }
}