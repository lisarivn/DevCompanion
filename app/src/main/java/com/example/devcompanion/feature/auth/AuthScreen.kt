package com.example.devcompanion.feature.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AuthContent(
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (val state = uiState) {
            AuthUiState.Idle -> {
                Text("Connect your GitHub account to DevCompanion.")

                Button(
                    onClick = viewModel::startLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login with GitHub")
                }
            }

            AuthUiState.Loading -> {
                CircularProgressIndicator()
                Text("Requesting authorization code...")
            }

            is AuthUiState.WaitingForAuthorization -> {
                Text("Enter this code on GitHub:")

                Text(
                    text = state.userCode,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(state.verificationUri)
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open GitHub")
                }

                CircularProgressIndicator()
                Text("Waiting for authorization...")

                OutlinedButton(
                    onClick = viewModel::cancelLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }

            AuthUiState.Authenticated -> {
                Text(
                    text = "✓ Connected to GitHub",
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = viewModel::logout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }

            is AuthUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = viewModel::startLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}