@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeUiApi::class
)

package com.example.organize3.bankAccounts

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.organize3.OrganizeTopAppBar
import com.example.organize3.R
import com.example.organize3.model.Bank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBankAccountScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp:() -> Unit,
    goToAddOtherDetailScreen:(String, Int) -> Unit,
    bankViewModel: BankViewModel = viewModel()) {
    val bankUiState by bankViewModel.bankAccount.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            OrganizeTopAppBar(
                title = stringResource(id = R.string.select_bank),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
                )
        }
        ) { values ->
        Screen(modifier.padding(values),
            searchedBankName = bankUiState.bankName,
            bankViewModel = bankViewModel,
            goToAddOtherDetailScreen = goToAddOtherDetailScreen,
            onKeyboardDone = { keyboardController?.hide()},
            onBankSearchChanged = { search ->
            bankViewModel.updateBankSearch(search)
        })
    }
}


@Composable
fun Screen(modifier: Modifier = Modifier,
           searchedBankName: String,
           onKeyboardDone:() -> Unit,
           bankViewModel: BankViewModel,
           goToAddOtherDetailScreen: (String, Int) -> Unit,
           onBankSearchChanged:(String) -> Unit) {

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

            OutlinedTextField(
                value = searchedBankName,
                onValueChange = {
                    onBankSearchChanged(it)
                    bankViewModel.searchBankName(it)},
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                isError = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone()}
                ),
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                placeholder = { Text(text = stringResource(id = R.string.search)) })
//        Text(
//            text = stringResource(id = R.string.popular_bank),
//            style = MaterialTheme.typography.bodyLarge,
//            modifier = Modifier.padding(horizontal = 20.dp)
//        )
//        PopularBanks(Modifier.padding(top = 12.dp))
        Text(
            text = stringResource(id = R.string.all_other_banks),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
        )
        AllBanks(bankList = bankViewModel.bankList.value, goToAddOtherDetailScreen = goToAddOtherDetailScreen)
//        BankItem(R.string.ambarnath_bank)
//        BankItem(R.string.au_small_bank)
//        BankItem(R.string.abhyuday_bank)
//        BankItem(R.string.arunachal_pradesh_bank)
//        BankItem(R.string.adarsh_bank)
//        BankItem(R.string.ahmednagar_bank)
//        BankItem(R.string.ahmednagar_shahar_bank)
//        BankItem(R.string.airtel_bank)
//        BankItem(R.string.ajanta_bank)
//        BankItem(R.string.akhand_anand_bank)
//        BankItem(R.string.allahabad_bank)
//        BankItem(R.string.ambajogai_bank)
//        BankItem(R.string.amreli_jilla_bank)
//        BankItem(R.string.andhra_bank)
//        BankItem(R.string.andhra_pradesh_bank)
//        BankItem(R.string.andhra_pradesh_mahesh_bank)
//        BankItem(R.string.andhra_pragathi_bank)
//        BankItem(R.string.apna_sahakari_bank)
//        BankItem(R.string.arihant_bank)
//        BankItem(R.string.arvind_bank)
//        BankItem(R.string.assam_gramin_bank)
//        BankItem(R.string.axis_bank, R.drawable.axis_bank)
    }
}

@Composable
fun AllBanks(
    modifier: Modifier = Modifier,
    goToAddOtherDetailScreen: (String, Int) -> Unit,
    bankList: List<Bank>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(4.dp)
    ) { items(bankList) { item ->
        BankItem(bName = item.bankName, bLogo = item.bankLogo, goToAddOtherDetailScreen = goToAddOtherDetailScreen)
    }
    }
}

@Composable
fun BankItem(
        bName: String,
        goToAddOtherDetailScreen: (String, Int) -> Unit,
        @DrawableRes bLogo: Int = R.drawable.bank_image_2,
        modifier: Modifier = Modifier) {
            Row(modifier = modifier
                .fillMaxWidth()
                .clickable { goToAddOtherDetailScreen(bName, bLogo) }
                .padding(vertical = 6.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .padding(8.dp),
                    painter = painterResource(id = bLogo),
                    contentDescription = "Bank logo",
                    contentScale = ContentScale.Crop)
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                    ,
                    text = bName,
                    style = MaterialTheme.typography.titleMedium)
            }
        }

//@Composable
//fun PopularBanks(modifier: Modifier = Modifier) {
//    Column(modifier = modifier) {
//        PopularBankRow(
//            bankName_one = R.string.bank_of_baroda,
//            banklogo_one = R.drawable.bank_of_baroda_logo,
//            bankName_two = R.string.icici_bank,
//            banklogo_two = R.drawable.icici_logo
//        )
//        PopularBankRow(
//            bankName_one = R.string.hdfc_bank,
//            banklogo_one = R.drawable.hdfc_logo,
//            bankName_two = R.string.axis_bank,
//            banklogo_two = R.drawable.axis_bank
//        )
//        PopularBankRow(
//            bankName_one = R.string.state_bank_india,
//            banklogo_one = R.drawable.sbi_logo,
//            bankName_two = R.string.bank_of_india,
//            banklogo_two = R.drawable.boi
//        )
//    }
//}

@Composable
fun PopularBankRow(
    modifier: Modifier = Modifier,
    @StringRes bankName_one: Int,
    @DrawableRes banklogo_one: Int,
    @StringRes bankName_two: Int,
    @DrawableRes banklogo_two: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        PopularBankCard(
            logo = banklogo_one,
            bankName = bankName_one,
            modifier = Modifier
                .weight(1f)
        )
        PopularBankCard(
            logo = banklogo_two,
            bankName = bankName_two,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun PopularBankCard(
    modifier: Modifier = Modifier,
    @DrawableRes logo: Int,
    @StringRes bankName: Int
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .size(120.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(55.dp)
                        .clip(CircleShape),
                    painter = painterResource(id = logo),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Bank Logo"
                )
                Text(
                    text = stringResource(id = bankName),
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun ScreenPreview() {
//    Screen()
//}

@Preview(showBackground = true)
@Composable
fun PopularBankCardPreview() {
    PopularBankCard(logo = R.drawable.union_bank, bankName = R.string.union_bank)
}

@Preview(showBackground = true)
@Composable
fun PopularBankRowPreview() {
    PopularBankRow(
        banklogo_one = R.drawable.bank_of_baroda_logo, bankName_one = R.string.bank_of_baroda,
        banklogo_two = R.drawable.icici_logo, bankName_two = R.string.icici_bank
    )
}

//@Preview(showBackground = true)
//@Composable
//fun PopularBankPreview() {
//    PopularBanks()
//}
//
//@Preview(showBackground = true)
//@Composable
//fun BankItemPreview() {
//    BankItem(bName = "Axis Bank", bLogo = R.drawable.bank_image_2)
//}

//@Preview(showBackground = true)
//@Composable
//fun AllBankPreview() {
//    AllBanks(bank)
//}