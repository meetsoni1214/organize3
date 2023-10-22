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
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DrawerState
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
import coil.compose.AsyncImage
import com.example.organize3.data.DataSource.Categories
import com.example.organize3.data.folderWithNotes.Folder
import com.example.organize3.data.folderWithNotes.FolderWithNotes
import com.example.organize3.folder.FolderHomeViewModel
import com.example.organize3.presentation.sign_in.UserData
import com.example.organize3.ui.theme.shapes
import kotlinx.coroutines.launch

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    drawerState: androidx.compose.material3.DrawerState,
    userData: UserData?,
    onSignOut: () -> Unit,
    closeDrawer: () -> Unit,
    onNavigationIconClick: () -> Unit,
    viewModel: FolderHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onFolderSelected: (Int, String) -> Unit,
    onArchivedSelected: () -> Unit,
        onItemSelected: (Int) -> Unit) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var fName by rememberSaveable { mutableStateOf("") }
    val folderHomeUiState by viewModel.folderHomeUiState.collectAsState()
    MyNavigationDrawer(
        drawerState = drawerState,
        userData = userData,
        onSignOut = onSignOut,
        onFolderSelected = onFolderSelected,
        onCategorySelected = onItemSelected,
        folderList = folderHomeUiState.folderList,
        closeDrawer = closeDrawer,
        onArchivedSelected = onArchivedSelected) {
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(),
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    showDialog.value = true
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
                    title = stringResource(id = R.string.choose_category),
                    onNavigationIconClick = onNavigationIconClick,
                    canNavigateBack = false)
            },

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
//                    coroutineScope.launch {
//                        val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
//                            message = "Folder deleted!",
//                            actionLabel = "Undo"
//                        )
//                        when (snackBarResult) {
//                            SnackbarResult.Dismissed -> Log.d("SnackBarDemo", "Dismissed")
//                            SnackbarResult.ActionPerformed -> {
//                            }
//                        }
//                    }
                }
            )
        }
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
            androidx.compose.material3.OutlinedButton(onClick = onDeleteCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = onCreateFolder,
                enabled = fname.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.create))
            }
        }
    )
}

@Composable
fun DrawerHeader(
    userData: UserData?,
    modifier: Modifier = Modifier
) {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
                ){
            if (userData?.profilePictureUrl != null) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = stringResource(id = R.string.user),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(70.dp),
                    contentScale = ContentScale.Crop
                )
            }else {
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(70.dp),
                    painter = painterResource(id = R.drawable.user_image),
                    contentDescription = stringResource(id = R.string.user),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (userData?.userName != null) {
                Text(
                    text = userData.userName,
                    style = MaterialTheme.typography.titleMedium,
                )
            }else {
                Text(
                    text = stringResource(id = R.string.facebook_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (userData?.userEmail != null) {
                Text(
                    text = userData.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }else {
                Text(
                    text = stringResource(id = R.string.facebook_id),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
}

@Composable
fun DrawerMenuItem(
    iconDrawableId: Int,
    text: String,
    onItemClick:() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconDrawableId),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
}

@Composable
fun DrawerMenuInsideItem(
    modifier: Modifier = Modifier,
    iconDrawableId: Int,
    text: Int = 0,
    folderId: Int = 0,
    folderName: String = "",
    isFolderSelected: Boolean = false,
    onCategoryClick: (Int) -> Unit = {},
    closeDrawer:() -> Unit,
    onFolderClick: (Int, String) -> Unit = { _, _ ->

    },
) {
    val modifiedText = if (isFolderSelected) folderName else stringResource(id = text)

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    if (isFolderSelected) {
                        onFolderClick(folderId, folderName)
                    } else {
                        onCategoryClick(text)
                    }
                    closeDrawer()
                }
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(45.dp)
                    .padding(12.dp),
                painter = painterResource(id = iconDrawableId),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = modifiedText, style = MaterialTheme.typography.bodyMedium)
        }
}

@Composable
fun DrawerBody(
    modifier: Modifier = Modifier,
    folderList: List<FolderWithNotes>,
    onFolderSelected: (Int, String) -> Unit,
    onArchivedSelected: () -> Unit,
    onCategorySelected: (Int) -> Unit,
    onSignOut: () -> Unit,
    closeDrawer: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        var showCategories by rememberSaveable{mutableStateOf(false)}
        var showFolders by rememberSaveable{mutableStateOf(false)}
        DrawerMenuItem(
            iconDrawableId = if (showCategories) R.drawable.ic_dropup else R.drawable.ic_dropdown,
            text = stringResource(id = R.string.category),
            onItemClick = {
                showCategories = !showCategories
            })
        if (showCategories) {
            NavigationCategories(
                onCategorySelected = onCategorySelected,
                closeDrawer = closeDrawer
            )
        }
        DrawerMenuItem(
            iconDrawableId =if (showFolders) R.drawable.ic_dropup else R.drawable.ic_dropdown,
            text = stringResource(id = R.string.folders),
            onItemClick = {
                showFolders = !showFolders
            })
        if (showFolders) {
            NavigationFolders(
                folderList = folderList,
                onFolderSelected = onFolderSelected,
                closeDrawer = closeDrawer
            )
        }
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_archived,
            text = stringResource(id = R.string.archived),
            onItemClick = {
                onArchivedSelected()
                closeDrawer()
            })
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_settings,
            text = stringResource(id = R.string.settings),
            onItemClick = { /*TODO*/ })
        DrawerMenuItem(
            iconDrawableId = R.drawable.ic_login,
            text = stringResource(id = R.string.log_out),
            onItemClick = onSignOut)
    }
}

@Composable
fun MyNavigationDrawer(
    drawerState: androidx.compose.material3.DrawerState,
    userData: UserData?,
    onSignOut: () -> Unit,
    onFolderSelected: (Int, String) -> Unit,
    onCategorySelected: (Int) -> Unit,
    folderList: List<FolderWithNotes>,
    closeDrawer: () -> Unit,
    onArchivedSelected: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    DrawerHeader(userData = userData)
                    DrawerBody(
                        folderList = folderList,
                        onFolderSelected = onFolderSelected,
                        onArchivedSelected = onArchivedSelected,
                        onCategorySelected = onCategorySelected,
                        onSignOut = onSignOut,
                        closeDrawer = closeDrawer
                    )
                }
            )
        },
        content = content
    )
}
@Composable
fun NavigationFolders(
    modifier: Modifier = Modifier,
    folderList: List<FolderWithNotes>,
    onFolderSelected: (Int, String) -> Unit,
    closeDrawer: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        items(items = folderList, key = {it.folder.id}) { item ->
            DrawerMenuInsideItem(
                iconDrawableId = R.drawable.folder,
                folderName = item.folder.folderName,
                isFolderSelected = true,
                onFolderClick = onFolderSelected,
                folderId = item.folder.id,
                closeDrawer = closeDrawer
            )
        }
    }
}

@Composable
fun NavigationCategories(
    modifier: Modifier = Modifier,
    onCategorySelected: (Int) -> Unit,
    closeDrawer: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        DrawerMenuInsideItem(
            iconDrawableId = R.drawable.bank_image_2,
            text = R.string.bank_category,
            onCategoryClick = onCategorySelected,
            closeDrawer = closeDrawer
            )
        DrawerMenuInsideItem(
            iconDrawableId = R.drawable.website_logo,
            text = R.string.application_category,
            onCategoryClick = onCategorySelected,
            closeDrawer = closeDrawer
        )
        DrawerMenuInsideItem(
            iconDrawableId = R.drawable.email_icon,
            text = R.string.email_category,
            onCategoryClick = onCategorySelected,
            closeDrawer = closeDrawer
        )
    }
}
@OptIn(ExperimentalMaterialApi::class)
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                                DismissValue.Default -> MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
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
                                .align(alignment = Alignment.CenterVertically),
                            shape = shapes.medium,
                            backgroundColor  = MaterialTheme.colorScheme.secondaryContainer
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
        .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
        ) {
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
                style = MaterialTheme.typography.titleLarge
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
            .padding(vertical = 8.dp)
        ,
        shape = MaterialTheme.shapes.medium,
        onClick = {
        onCategorySelected(textId)},
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(194.dp)
                    .fillMaxWidth()
            )
            Text(text = stringResource(id = textId),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        }
    }
    }
