package academy.bangkit.jetvive.ui.screen.profile

import academy.bangkit.jetvive.R
import academy.bangkit.jetvive.data.source.remote.request.LogoutRequest
import academy.bangkit.jetvive.helper.ViewModelFactory
import academy.bangkit.jetvive.ui.common.UiState
import academy.bangkit.jetvive.ui.components.Alert
import academy.bangkit.jetvive.ui.components.LoadingDialog
import academy.bangkit.jetvive.ui.screen.onboarding.OnboardingViewModel
import academy.bangkit.jetvive.ui.theme.JetViVeTheme
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    navigateToSignIn: () -> Unit,
    viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
    launchSnackbar: () -> Unit
) {
    viewModel.getLogin()
    val loginData by viewModel.loginData.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        LoadingDialog()
    }
    viewModel.uiState.collectAsState().value.let { uiState ->
        when (uiState) {
            is UiState.Loading -> {
                isLoading = true
                viewModel.getUser(accessToken = loginData?.accessToken.toString())
            }
            is UiState.Success -> {
                isLoading = false
                ProfileContent(
                    userImage = R.drawable.jetpack_compose,
                    userName = uiState.data.data.name,
                    username = uiState.data.data.username,
                    userEmail = uiState.data.data.email,
                    userPhoneNumber = uiState.data.data.phoneNumber,
                    userAddress = uiState.data.data.address,
                    userGender = uiState.data.data.gender,
                    onBackClick = onBackClick,
                    navigateToSignIn = navigateToSignIn,
                    launchSnackbar = launchSnackbar
                )
            }
            is UiState.Error -> {}
        }
    }
}

@Composable
fun ProfileContent(
    userImage: Int? = null,
    userName: String? = null,
    username: String? = null,
    userEmail: String? = null,
    userPhoneNumber: String? = null,
    userAddress: String? = null,
    userGender: String? = null,
    onBackClick: () -> Unit,
    navigateToSignIn: () -> Unit,
    launchSnackbar: () -> Unit,
    viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
    modifier: Modifier = Modifier
) {
    viewModel.getLogin()
    val loginData by viewModel.loginData.collectAsState()
    val logoutStatus by viewModel.logoutStatus.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (isLoading) {
        LoadingDialog()
    }

    LaunchedEffect(logoutStatus) {
        if (logoutStatus is UiState.Success) {
            Toast.makeText(context, R.string.logout_successful, Toast.LENGTH_SHORT).show()
            navigateToSignIn()
        } else if (logoutStatus is UiState.Error) {
            Toast.makeText(context, R.string.login_failed, Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                IconButton(
                    onClick = { onBackClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier
                            .size(25.dp)
                    )
                }

                val showDialog = remember { mutableStateOf(false) }
                if (showDialog.value) {
                    Alert(title = stringResource(
                        R.string.logout),
                        name = stringResource(R.string.alert_name),
                        showDialog = showDialog.value,
                        onConfirm = {
                            showDialog.value = false
                            isLoading = true
                            viewModel.logout(LogoutRequest(
                                refreshToken = loginData?.refreshToken.toString()
                            ))
                        },
                        onDismiss = { showDialog.value = false }
                    )
                }

                IconButton(
                    onClick = {
                        showDialog.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = stringResource(R.string.logout),
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }
            Image(
                painter = painterResource(userImage ?: 0),
                contentDescription = stringResource(R.string.user_image),
                modifier = Modifier
                    .size(150.dp)
            )
            Text(
                text = userName ?: "(Not Set)",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Divider()
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.user_information),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold

                )
                IconButton(
                    onClick = { launchSnackbar() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.username_icon)
                    )
                    Text(
                        text = username ?: "(Not Set)"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.email_icon)
                    )
                    Text(
                        text = userEmail ?: "(Not Set)"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = stringResource(R.string.phone_number_icon)
                    )
                    Text(
                        text = userPhoneNumber ?: "(Not Set)"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.address_icon)
                    )
                    Text(
                        text = userAddress ?: "(Not Set)"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = stringResource(R.string.gender_icon)
                    )
                    Text(
                        text = userGender ?: "(Not Set)"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContentPreview() {
    JetViVeTheme {
        ProfileContent(
            userImage = R.drawable.jetpack_compose,
            userName = stringResource(R.string.user_name),
            username = stringResource(R.string.username),
            userEmail = stringResource(R.string.user_email),
            userPhoneNumber = stringResource(R.string.user_phone_number),
            userAddress = stringResource(R.string.user_address),
            userGender = stringResource(R.string.user_gender),
            onBackClick = {},
            navigateToSignIn = {},
            launchSnackbar = {}
        )
    }
}