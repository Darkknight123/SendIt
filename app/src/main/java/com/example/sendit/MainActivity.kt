package com.example.sendit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.sendit.logic.App.NetworkModule
import com.example.sendit.logic.App.NetworkTokenManager
import com.example.sendit.logic.model.ExistingUserResponse
import com.example.sendit.logic.model.Transactions
import com.example.sendit.logic.model.UserResponseModel
import com.example.sendit.logic.viewmodel.ProfileViewModel
import com.example.sendit.screens.HomeScreen
import com.example.sendit.screens.LoadingIndicator
import com.example.sendit.screens.LoginScreen
import com.example.sendit.ui.theme.SendItTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    private var isLoggedIn = mutableStateOf(false)
    private var isLoading = mutableStateOf(false)  // Track loading state

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        enableEdgeToEdge()

        setContent {
            SendItTheme {
                        if (isLoading.value) {
                            LoadingIndicator()  // Show loader
                        } else {
                            // Use if-else to switch between screens based on login state
                            if (isLoggedIn.value) {
                                // You might want to fetch actual transactions here
                                val mockTransactions = listOf(
                                    Transactions("client1", 200, false),
                                    Transactions("client1", 200, false),
                                    Transactions("client1", 200, false),
                                    Transactions("client1", 200, false)
                                )
                                HomeScreen(mockTransactions)
                            } else {
                                LoginScreen(onGoogleSignInClick = { launchGoogleSignIn() })
                            }
                        }
            }
        }
    }

    private fun launchGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                account?.let {
                    handleSignInResult(it)
                }
            } catch (e: ApiException) {
                e.printStackTrace() // Handle error
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mFirebaseUser = FirebaseAuth.getInstance().currentUser
                    mFirebaseUser?.getIdToken(true)?.addOnSuccessListener { getTokenResult: GetTokenResult ->
                        getTokenResult.token?.let { NetworkTokenManager.updateToken(it) }
                        NetworkModule.resetRetrofit()

                        lifecycleScope.launch {
                            try {
                                // Set loading state to true
                                isLoading.value = true

                                val viewModel = ProfileViewModel()
                                viewModel.registerUser(
                                    name = account.displayName ?: "",
                                    email = account.email ?: "",
                                    photoUrl = account.photoUrl?.toString()
                                ).fold(
                                    onSuccess = { response ->
                                        when(response) {
                                            is UserResponseModel -> {
                                                Log.d("Registration", "New user registered: ${response.Payload.uid}")
                                            }
                                            is ExistingUserResponse -> {
                                                Log.d("Registration", "Existing user: ${response.Payload}")
                                            }
                                        }
                                        isLoggedIn.value = true
                                    },
                                    onFailure = { error ->
                                        Log.e("Registration", "Error: ${error.message}")
                                        isLoggedIn.value = true  // Still proceed to home screen
                                    }
                                )
                            } catch (e: Exception) {
                                Log.e("Registration", "Exception: ${e.message}")
                                isLoggedIn.value = true  // Proceed even if there's an error
                            } finally {
                                // Set loading state to false after API call is complete
                                isLoading.value = false
                            }
                        }
                    }
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}




