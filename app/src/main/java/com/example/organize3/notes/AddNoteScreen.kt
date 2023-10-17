@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.organize3.notes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.organize3.AppViewModelProvider
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.appUi.NoteUiState
import com.example.organize3.appUi.toNote
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
    val isArchived = viewModel.isArchived
    val foldersList by viewModel.foldersUiState.collectAsState()
    var isBottomBarVisible by remember { mutableStateOf(true) }
    val multiplePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = {uris ->
            val uriStrings: MutableList<String> = mutableListOf()
            for (uri in uris) {
                uriStrings.add(uri.toString())
            }

            viewModel.updateUiState(uiState.copy(uris = uriStrings))})
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = "",
                showDone = true,
                isArchived = (isArchived == 1),
                foldersList = foldersList.folderList,
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
                moveNote = { folderId ->
                    viewModel.updateUiState(uiState.copy(folderId = folderId))
                           coroutineScope.launch {
                               viewModel.updateNote()
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
                        viewModel.deleteNote(uiState.toNote())
                        navigateBack()
                    }
                },
                archive = {
                          coroutineScope.launch {
                              viewModel.archiveNote()
                              navigateBack()
                          }
                },
                unArchive = {
                            coroutineScope.launch {
                                viewModel.unArchiveNote()
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
        },
        bottomBar = {
            if (isBottomBarVisible) {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = {
                            multiplePhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.vector_image),
                                contentDescription = null)
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_brush),
                                contentDescription = null)
                        }
                    },
                )
            }
        }
    ) { innerPadding ->
        NoteEntryBody(
            modifier = modifier.padding(innerPadding),
            onNoteValueChange = viewModel::updateUiState,
            noteUiState = uiState,
            makeBottomBarVis = { value ->
                isBottomBarVisible = value
            },
            uris = uiState.uris
        )
    }
}

@Composable
fun NoteEntryBody(
    modifier: Modifier = Modifier,
    onNoteValueChange: (NoteUiState) -> Unit,
    makeBottomBarVis: (Boolean) -> Unit,
    uris: List<String>,
    noteUiState: NoteUiState
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {

        item {
            Column (
                modifier = Modifier.fillMaxWidth()
                    ){
                TransparentTextField(
                    noteUiState = noteUiState,
                    hint = stringResource(id = R.string.title),
                    onNoteValueChange = onNoteValueChange,
                    onNextClicked = {
                        scope.launch {
                            scrollState.animateScrollTo(Int.MAX_VALUE)
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    },
                    isHintVisible = noteUiState.title.isBlank(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = MaterialTheme.typography.headlineMedium.fontSize),
                    onFocusChange = {},
                    makeBottomBarVis = makeBottomBarVis
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        items(uris) { uri ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Gallery image",
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        item {
            TransparentTextFieldContent(
                noteUiState = noteUiState,
                hint = stringResource(id = R.string.start_writing),
                onNoteValueChange = onNoteValueChange,
                singleLine = false,
                isHintVisible = noteUiState.content.isBlank(),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize),
                onFocusChange = {},
                makeBottomBarVis = makeBottomBarVis
            )
        }
    }
}

@Composable
fun TransparentTextField(
    noteUiState: NoteUiState,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onNoteValueChange: (NoteUiState) -> Unit,
    textStyle: TextStyle,
    singleLine: Boolean = false,
    makeBottomBarVis: (Boolean) -> Unit,
    onNextClicked: () -> Unit = {},
    onFocusChange: (FocusState) -> Unit
) {
    Box(modifier = modifier) {
        BasicTextField(
            value = noteUiState.title,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
            onValueChange = {
                onNoteValueChange(noteUiState.copy(title = it)) },
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNextClicked() }
            ),
            singleLine = singleLine,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    makeBottomBarVis(false)
                }
                .onFocusChanged {
                    onFocusChange(it)
                }
        )
        if (isHintVisible) {
            Text(
                text = hint,
                style = textStyle
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
    makeBottomBarVis: (Boolean) -> Unit,
    textStyle: TextStyle,
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .clickable {
                makeBottomBarVis(true)
            }
            .fillMaxWidth()
            .fillMaxHeight()) {
        BasicTextField(
            value = noteUiState.content,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
            singleLine = singleLine,
            onValueChange = { onNoteValueChange(noteUiState.copy(content = it)) },
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                }
            ),
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
            )
        }
    }
}