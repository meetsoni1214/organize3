@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package com.example.organize3

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.organize3.data.DataSource.Categories
import com.example.organize3.data.folderWithNotes.Folder
import com.example.organize3.data.folderWithNotes.FolderWithNotes
import com.example.organize3.folder.FolderHomeViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: FolderHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onFolderSelected: (Int, String) -> Unit,
        onItemSelected: (Int) -> Unit) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    var fName by rememberSaveable { mutableStateOf("") }
    val folderHomeUiState by viewModel.folderHomeUiState.collectAsState()
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
             FloatingActionButton(onClick = {
                 showDialog.value = true
             }) {
                 Icon(imageVector = Icons.Default.Add, contentDescription = null)
             }
        },
        topBar = {
        OrganizeTopAppBar(title = stringResource(id = R.string.choose_category),
            canNavigateBack = false)
    }
    ) { innerPadding ->
        CategoriesBody(
            modifier = modifier.padding(innerPadding),
            onCategorySelected = onItemSelected,
            folderList = folderHomeUiState.folderList,
            onFolderSelected = onFolderSelected,
            deleteFolder = {folder, id ->
                coroutineScope.launch {
                    viewModel.deleteFolder(folder, id)
                }
                coroutineScope.launch {
                    val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Folder deleted!",
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
    if (showDialog.value) {
        FolderNameDialog(
            onDeleteCancel = {
                showDialog.value = false
            },
            folderName = {fName = it },
            onCreateFolder = {
                coroutineScope.launch {
                    viewModel.insertFolder(fName)
                }
                showDialog.value = false
            }
        )
    }
}

@Composable
fun FolderNameDialog(
    modifier: Modifier = Modifier,
    onDeleteCancel:() -> Unit,
    folderName:(String) -> Unit,
    onCreateFolder:() -> Unit
) {
    var fname by rememberSaveable() { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(id = R.string.name_folder))},
        text = {
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
                    ){
                Text(text = stringResource(id = R.string.enter_folder_name))
                OutlinedTextField(
                    value = fname,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                                    fname = it
                        folderName(fname)
                    },
                    singleLine = true
                )
            }
        },
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onCreateFolder) {
                Text(text = stringResource(id = R.string.create))
            }
        }
    )
}

@Composable
fun CategoriesBody(
    modifier: Modifier = Modifier,
    folderList: List<FolderWithNotes>,
    onFolderSelected: (Int, String) -> Unit,
    deleteFolder: (Folder, Int) -> Unit,
    onCategorySelected: (Int) -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(16.dp)) {
            items(Categories) { item ->
                CategoryCard(imageId = item.ImageResourceID, textId = item.stringResourceId, onCategorySelected = {itemId ->
                    onCategorySelected(itemId)
                })
            }
            items(items = folderList, key = {it.folder.id}) {item ->
                val dismissState = rememberDismissState()

                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                    deleteFolder(item.folder, item.folder.id)
                }
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier
                        .padding(vertical = Dp(1f)),
                    directions = setOf(
                        DismissDirection.EndToStart
                    ),
                    dismissThresholds = {direction ->
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
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = Dp(20f)),
                            contentAlignment = alignment
                        ) {
                            Icon (
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
                            FolderCard(
                                folder = item.folder,
                                onFolderSelected = onFolderSelected
                            )
                        }
                    }
                )

            }
        }
//        LazyColumn(
//            modifier = Modifier.fillMaxWidth(),
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            contentPadding = PaddingValues(16.dp)
//        ) {
//            items(items = folderList, key = {it.id}) {item ->
//                FolderCard(folder = item)
//            }
//        }
    }
}

@Composable
fun FolderCard(
    modifier: Modifier = Modifier,
    folder: Folder,
    onFolderSelected:(Int, String) -> Unit
) {
    Card(modifier = modifier
        .padding(4.dp)
        .clickable { onFolderSelected(folder.id, folder.folderName) }
        .fillMaxWidth()) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.folder),
                contentDescription = stringResource(id = R.string.folder),
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .padding(6.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = folder.folderName,
                modifier = Modifier.padding(6.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    @DrawableRes imageId: Int,
    @StringRes  textId: Int,
    onCategorySelected: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .padding(vertical = 12.dp)
        , onClick = {
        onCategorySelected(textId)}) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(194.dp)
                    .fillMaxWidth()
            )
            Text(text = stringResource(id = textId),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        }
    }
    }


//
//@Preview(showBackground = true)
//@Composable
//fun CategoryPreview() {
//    Organize3Theme {
//        Categorycard(imageId = R.drawable.bank_category_image, textId = R.string.bank_category, modifier = Modifier.padding(8.dp))
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CategoriesPreview() {
//    Organize3Theme {
//        CategoryScreen()
//    }
//}
