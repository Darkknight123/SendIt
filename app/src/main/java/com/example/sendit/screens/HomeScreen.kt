package com.example.sendit.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sendit.logic.model.DepositModel
import com.example.sendit.logic.model.Transactions
import com.example.sendit.logic.model.User
import com.example.sendit.logic.model.UserResponseModel
import com.example.sendit.logic.viewmodel.ProfileViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.Result
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(transactions: List<Transactions>) {
    // Track the current screen
    var currentScreen by remember { mutableStateOf(Screen.History) }

    // Track whether the dialog is shown
    var showDialog by remember { mutableStateOf(false) }

    // Track user details
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var postalAddress by remember { mutableStateOf("") }
    var telephone1 by remember { mutableStateOf("") }
    var telephone2 by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val profileViewModel: ProfileViewModel = viewModel()
    // Variable to store the result
    var userDetailsResult by remember { mutableStateOf<Result<UserResponseModel>>(Result.failure(Exception("No data"))) }

    // Fetch user details when the screen is first displayed
    LaunchedEffect(Unit) {
        // Launch coroutine to fetch user details
        userDetailsResult = profileViewModel.fetchUserDetails()
        Log.e("TAG", "HomeScreen: " + Gson().toJson(userDetailsResult) )
    }

// Handle the result of the userDetails request
    userDetailsResult.onSuccess { userResponseModel ->
        // Handle the success result
        userResponseModel?.let {
            email = it.Payload.email
            firstName = it.Payload.firstName
            postalAddress = it.Payload.postalAddress ?: ""
            telephone1 = it.Payload.telephone1 ?: ""
            telephone2 = it.Payload.telephone2 ?: ""
            username = it.Payload.username
            lastName = it.Payload.lastName
        }
    }.onFailure { exception ->
        // Handle error (e.g., show an error message)
        // You can add some UI logic here if needed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {

        Text(
            text = username,
            modifier = Modifier
                .padding(20.dp)
                .clickable { showDialog = true },
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 22.sp,
        )

        // Show the dialog when `showDialog` is true
        if (showDialog) {
            UserDetailsDialog(
                email = email,
                firstName = firstName,
                postalAddress = postalAddress,
                telephone1 = telephone1,
                telephone2 = telephone2,
                username = username,
                lastName = lastName,
                onDismiss = { showDialog = false },
                onUpdate = { updatedDetails ->
                    email = updatedDetails.email.toString()
                    firstName = updatedDetails.firstName.toString()
                    postalAddress = updatedDetails.postalAddress.toString()
                    telephone1 = updatedDetails.telephone1.toString()
                    telephone2 = updatedDetails.telephone2.toString()
                    username = updatedDetails.username.toString()
                    lastName = updatedDetails.lastName.toString()
                    showDialog = false
                }
            )
        }

        // Display balance card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF491B6D))
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = " Usd 3000",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "20 ksh",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "→",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // Navigation buttons for History, Deposit, Withdraw, Send
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "History",
                modifier = Modifier
                    .clickable { currentScreen = Screen.History }
                    .padding(10.dp)
            )
            Text(
                text = "Deposit",
                modifier = Modifier
                    .clickable { currentScreen = Screen.Deposit }
                    .padding(10.dp)
            )
            Text(
                text = "Withdraw",
                modifier = Modifier
                    .clickable { currentScreen = Screen.Withdraw }
                    .padding(10.dp)
            )
            Text(
                text = "Send",
                modifier = Modifier
                    .clickable { currentScreen = Screen.SendMoney }
                    .padding(10.dp)
            )
        }

        // Conditional display based on `currentScreen`
        when (currentScreen) {
            Screen.History -> {
                // Display the transaction list (History screen)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionListItem(transaction)
                    }
                }
            }
            Screen.SendMoney -> {
                // Show the Send Money form
                SendMoney()
            }
            Screen.Deposit ->{
                DepositForm()
            }
            else -> {
                Withdraw()
            }
        }
    }
}

// Enum to manage different screens
enum class Screen {
    History, Deposit, Withdraw, SendMoney
}

@Composable
fun TransactionListItem(transaction: Transactions) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Transaction to: ${transaction.name}")
            Text(text = "Amount: ${transaction.amount}")
        }
    }
}

@Composable
fun SendMoney() {
    Column {

        Text(text = "Enter Transfer Amount", modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = "",
            onValueChange = { /* handle input */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(text = "Amount") },
        )

        Spacer(modifier = Modifier.height(8.dp))

        ClientDetails()

        Button(
            onClick = { /* Handle send logic */ },
            colors = ButtonDefaults.buttonColors(Color(0xFF491B6D)),
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "Submit")
        }
    }
}

@Composable
fun ClientDetails() {
    Column {
        Text(text = "Recipient Email", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = "",
            onValueChange = { /* handle input */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(text = "Enter M-Pesa number") },
        )
    }
}

@Composable
fun DepositForm() {
    // Declare state variables using remember
    var bankName by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Get your ViewModel instance in composable scope
    val profileViewModel: ProfileViewModel = viewModel()

    // Handle deposit action when button is clicked
    val onDepositClick = {
        // Only proceed if fields are not blank
        if (bankName.isNotBlank() && currency.isNotBlank() && amount.isNotBlank()) {
            // Prepare deposit data model
            val depositModel = DepositModel(
                amount = amount.toInt(),
                currency = currency,
                transactionRef = "your-transaction-ref" // This could be dynamically generated
            )

            // Show loading indicator while deposit is being processed
            isLoading = true

            // Launch the suspend function inside a LaunchedEffect block
            LaunchedEffect(depositModel) {
                // API call to deposit (suspend function call)
                profileViewModel.deposit(depositModel).onSuccess {
                    successMessage = "Deposit Successful"
                    errorMessage = null
                }.onFailure {
                    successMessage = null
                    errorMessage = "Deposit Failed: ${it.message}"
                }
                isLoading = false
            }
        } else {
            errorMessage = "Please fill in all fields."
        }
    }

    // UI Content for Deposit Form
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Deposit Form",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Bank Name Field
        OutlinedTextField(
            value = bankName,
            onValueChange = { bankName = it },
            label = { Text(text = "Bank Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Currency Field
        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text(text = "Currency") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Amount Field
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(text = "Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // Show loading indicator while waiting for API response
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        // Show success or error messages
        successMessage?.let {
            Text(text = it, color = Color.Green, modifier = Modifier.padding(top = 8.dp))
        }
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        // Deposit Button
        Button(
            onClick = { onDepositClick() },  // Regular function, no composable context here
            colors = ButtonDefaults.buttonColors(Color(0xFF491B6D)),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Deposit")
        }
    }
}



@Composable
fun Withdraw(){

    var amount by remember { mutableStateOf("") }

    Column (modifier = Modifier.padding(16.dp)) {
        // Amount Field
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(text = "Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = { /* Handle deposit logic */ },
            colors = ButtonDefaults.buttonColors(Color(0xFF491B6D)),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Withdraw")
        }
    }
}

@Composable
fun UserDetailsDialog(
    email: String,
    firstName: String,
    postalAddress: String,
    telephone1: String,
    telephone2: String,
    username: String,
    lastName: String,
    onDismiss: () -> Unit,
    onUpdate: (User) -> Unit
) {
    // State for holding input values
    var emailInput by remember { mutableStateOf(email) }
    var firstNameInput by remember { mutableStateOf(firstName) }
    var postalAddressInput by remember { mutableStateOf(postalAddress) }
    var telephone1Input by remember { mutableStateOf(telephone1) }
    var telephone2Input by remember { mutableStateOf(telephone2) }
    var usernameInput by remember { mutableStateOf(username) }
    var lastNameInput by remember { mutableStateOf(lastName) }

    // Obtain ViewModel instance
    val profileViewModel: ProfileViewModel = viewModel()

    // Dialog layout
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update User Details") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = firstNameInput,
                    onValueChange = { firstNameInput = it },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = lastNameInput,
                    onValueChange = { lastNameInput = it },
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = postalAddressInput,
                    onValueChange = { postalAddressInput = it },
                    label = { Text("Postal Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = telephone1Input,
                    onValueChange = { telephone1Input = it },
                    label = { Text("Telephone 1") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = telephone2Input,
                    onValueChange = { telephone2Input = it },
                    label = { Text("Telephone 2") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val request = User(
                        email = emailInput,
                        firstName = firstNameInput,
                        postalAddress = postalAddressInput,
                        telephone1 = telephone1Input,
                        telephone2 = telephone2Input,
                        username = usernameInput,
                        lastName = lastNameInput
                    )

                    // Launch a coroutine for the API call
                    CoroutineScope(Dispatchers.Main).launch {
                        profileViewModel.updateUserDetails(request).onSuccess {
                            onUpdate(request) // Update UI on success
                            onDismiss()
                        }.onFailure {
                            // Handle error (e.g., show a toast)
                        }
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview
@Composable
fun HomePreview() {
    val mockTransactions = listOf(
        Transactions("client1", 200, false),
        Transactions("client2", 300, true),
        Transactions("client3", 400, false),
        Transactions("client4", 500, true)
    )
    HomeScreen(mockTransactions)
}
