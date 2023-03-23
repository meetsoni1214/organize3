@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.organize3

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    goToMainScreen:() -> Unit,
    goToRegisterScreen:() -> Unit
) {
    Scaffold (
        modifier = modifier.fillMaxSize()
            ){ values ->
        LoginScreenBody(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(values),
        goToMainScreen = goToMainScreen,
            goToRegisterScreen = goToRegisterScreen
        )
    }
}

@Composable
fun LoginScreenBody(
    modifier: Modifier = Modifier,
    goToMainScreen: () -> Unit,
    goToRegisterScreen: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(
                vertical = 32.dp,
                horizontal = 16.dp
            ),
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        LoginCard(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 28.dp),
            goToMainScreen = goToMainScreen
        )
        RegisterCard(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 90.dp),
            goToRegisterScreen = goToRegisterScreen
        )
    }
}

@Composable
fun LoginCard(
    modifier: Modifier = Modifier,
    goToMainScreen: () -> Unit,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false)}
    var password by rememberSaveable { mutableStateOf("") }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column (
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
            OutlinedTextField(
                value = "",
                onValueChange = {},
                singleLine = true,
                label = {Text(text = stringResource(id = R.string.email_id))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
                }
            )
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                singleLine = true,
                label = {Text(text = stringResource(id = R.string.password))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "")
                },
                trailingIcon = {
                    val image = if (passwordVisible) {
                        R.drawable.visibility_off
                    }else {
                        R.drawable.visibility
                    }
                    val description = if (passwordVisible) "Hide Password" else "Show Password"
                    IconButton(onClick = {passwordVisible = !passwordVisible}) {
                        Icon(painter = painterResource(id = image), contentDescription = description)
                    }
                }
            )
            Text(
                text = stringResource(id = R.string.forgot_pass),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.End)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                ,
                onClick = { goToMainScreen() }) {
                Text(text = stringResource(id = R.string.login_button_text))
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(12.dp),
                text = stringResource(id = R.string.or),
                style = MaterialTheme.typography.labelMedium
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                               goToMainScreen()
                    }
                ,
                colors = CardDefaults.cardColors(containerColor = Color.Cyan)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(28.dp),
                        contentDescription = stringResource(id = R.string.fb_logo))
                    Text(

                        text = stringResource(id = R.string.continue_google),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "")
                }
            }
            Card(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Cyan)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook_icon),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(28.dp),
                        contentDescription = stringResource(id = R.string.fb_logo))
                    Text(

                        text = stringResource(id = R.string.continue_fb),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "")
                }
            }
        }
    }
}

@Composable
fun RegisterCard(
    modifier: Modifier = Modifier,
    goToRegisterScreen: () -> Unit
) {
    Card (modifier.fillMaxWidth()){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_up_request),
                        fontSize = 15.sp
                    )
                    Text(
                        text = stringResource(id = R.string.sign_up),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { goToRegisterScreen() }
                    )
                }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewLoginScreen() {
//    LoginScreenBody()
//}