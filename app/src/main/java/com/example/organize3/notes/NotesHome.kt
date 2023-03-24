@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class
)

package com.example.organize3.notes

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.data.folderWithNotes.Note
import com.example.organize3.R
import kotlinx.coroutines.launch
import androidx.compose.material.Card as Card1

@Composable
fun NotesHome(
    modifier: Modifier = Modifier,
    onNavigateUp:() -> Unit,
    navigateBack:() -> Unit,
    navigateToNote:(Int, Int) -> Unit,
    onAddNote:(Int, Int) -> Unit,
    viewModel: NotesHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notesHomeUiState by viewModel.noteHomeUiState.collectAsState()
//    val folderUiState by viewModel.folder.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {onAddNote(viewModel.folderId, -1)}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        scaffoldState = scaffoldState,
        topBar = {
            OrganizeTopAppBar(
                title = viewModel.folderName,
                showFolderMenu = true,
                deleteEmail = {
                              coroutineScope.launch {
                                  viewModel.deleteFolder()
                              }
                    navigateBack()
                },
                duplicateEmail = {
                    coroutineScope.launch {
                        viewModel.duplicateFolder()
                    }
                    navigateBack()
                },
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
            ){ values ->
        NotesScreen(
            notesList = notesHomeUiState.notesList,
            onNoteClick = navigateToNote,
            modifier = modifier.padding(values),
            folderId = viewModel.folderId,
            deleteNote = {note ->
                coroutineScope.launch {
                    viewModel.deleteNote(note)
                }
                coroutineScope.launch {
                    val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Item deleted1",
                        actionLabel = "Undo"
                    )
                    when (snackBarResult) {
                        SnackbarResult.Dismissed -> Log.d("SnackBarDemo", "Dismissed")
                        SnackbarResult.ActionPerformed -> {
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    notesList: List<Note>,
    onNoteClick: (Int, Int) -> Unit,
    folderId: Int,
    deleteNote: (Note) -> Unit
    ) {
    if (notesList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_notes_added_message),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            NotesList(onNoteClick = { onNoteClick(folderId, it.id) }, deleteNote = deleteNote, notesList = notesList)
        }
    }
}

@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    notesList: List<Note>,
    deleteNote: (Note) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Card1(
                        elevation = animateDpAsState(
                            if (dismissState.dismissDirection != null) 4.dp else 0.dp
                        ).value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        NoteCard(onNoteClick = onNoteClick, note = note)
                    }
                }
            )
        }
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    onNoteClick: (Note) -> Unit,
    note: Note
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable { onNoteClick(note) }
            .fillMaxWidth()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.noteTitle,
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(top = 4.dp)
                    ,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = note.noteContent,
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(end = 4.dp, bottom = 4.dp)
                    ,
                    style = MaterialTheme.typography.labelMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5
                )
            }
        }
    }
}