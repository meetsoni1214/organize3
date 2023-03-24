@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.organize3.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.appUi.NoteUiState
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.s

@Composable
fun AddNoteScreen(
    modifier: Modifier = Modifier,
    onNavigateUp:() -> Unit,
    navigateBack:() -> Unit,
    viewModel: NoteEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.noteUiState
    val scaffoldState = rememberScaffoldState()
    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            OrganizeTopAppBar(
                title = "",
                showDone = true,
                saveNote = {
                           coroutineScope.launch {
                               if (viewModel.noteId != -1) {
                                   viewModel.updateNote()
                               }else {
                                   viewModel.saveNote()
                               }
                           }
                    navigateBack()
                },
                canNavigateBack = true,
                navigateUp = navigateBack,
                shareText = stringResource(
                    R.string.share_note,
                    uiState.title,
                    uiState.content
                ),
                shareSubject = stringResource(id = R.string.notes),
                deleteEmail = {
                    coroutineScope.launch {
                        viewModel.deleteNote()
                        navigateBack()
                    }
                },
                duplicateEmail = {
                    coroutineScope.launch {
                        viewModel.duplicateNote()
                        navigateBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        NoteEntryBody(
            modifier = modifier.padding(innerPadding),
            onNoteValueChange = viewModel::updateUiState,
            noteUiState = uiState
        )
    }
}

@Composable
fun NoteEntryBody(
    modifier: Modifier = Modifier,
    onNoteValueChange: (NoteUiState) -> Unit,
    noteUiState: NoteUiState
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TransparentTextField(
            noteUiState = noteUiState,
            hint = stringResource(id = R.string.title),
            onNoteValueChange = onNoteValueChange,
            singleLine = true,
            isHintVisible = noteUiState.title.isBlank(),
            textStyle = MaterialTheme.typography.headlineMedium,
            onFocusChange = {})
        Spacer(modifier = modifier.height(12.dp))
        TransparentTextFieldContent(
            noteUiState = noteUiState,
            hint = stringResource(id = R.string.start_writing),
            onNoteValueChange = onNoteValueChange,
            singleLine = false,
            isHintVisible = noteUiState.content.isBlank(),
            textStyle = MaterialTheme.typography.titleMedium,
            onFocusChange = {})
    }
}

@Composable
fun TransparentTextField(
    noteUiState: NoteUiState,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onNoteValueChange: (NoteUiState) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {
    Box(modifier = modifier) {
        BasicTextField(
            value = noteUiState.title,
            onValueChange = {
                onNoteValueChange(noteUiState.copy(title = it)) },
            textStyle = textStyle,
            singleLine = singleLine,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                }
        )
        if (isHintVisible) {
            Text(
                text = hint,
                style = textStyle,
                color = Color.DarkGray
            )
        }
    }
}


@Composable
fun TransparentTextFieldContent(
    noteUiState: NoteUiState,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onNoteValueChange: (NoteUiState) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
        BasicTextField(
            value = noteUiState.content,
            singleLine = singleLine,
            onValueChange = { onNoteValueChange(noteUiState.copy(content = it)) },
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                }
        )
        if (isHintVisible) {
            Text(
                text = hint,
                style = textStyle,
                color = Color.DarkGray
            )
        }
    }
}