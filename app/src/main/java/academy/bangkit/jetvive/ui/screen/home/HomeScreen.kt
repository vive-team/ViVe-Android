package academy.bangkit.jetvive.ui.screen.home

import academy.bangkit.jetvive.R
import academy.bangkit.jetvive.helper.SharedViewModel
import academy.bangkit.jetvive.helper.ViewModelFactory
import academy.bangkit.jetvive.helper.getMoodEmoji
import academy.bangkit.jetvive.helper.getPeriod
import academy.bangkit.jetvive.helper.getPeriodSky
import academy.bangkit.jetvive.helper.getMoodString
import academy.bangkit.jetvive.ui.common.UiState
import academy.bangkit.jetvive.ui.components.Alert
import academy.bangkit.jetvive.ui.components.GifImage
import academy.bangkit.jetvive.ui.components.LoadingBar
import academy.bangkit.jetvive.ui.components.TouristAttractionItem
import academy.bangkit.jetvive.ui.theme.JetViVeTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import java.util.Calendar

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit,
    navigateToSurvey: () -> Unit,
    navigateToDetail: () -> Unit,
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier
) {
    HomeContent(
        navigateToProfile = navigateToProfile,
        navigateToSurvey = navigateToSurvey,
        navigateToDetail = navigateToDetail,
        sharedViewModel = sharedViewModel
    )
}

@Composable
fun HomeContent(
    navigateToProfile: () -> Unit,
    navigateToSurvey: () -> Unit,
    navigateToDetail: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        viewModel.getLogin()

        val uiLoginState by viewModel.uiLoginState.collectAsState()

        TopSection(
            navigateToProfile = navigateToProfile
        )

        viewModel.uiSurveyState.collectAsState(initial = UiState.Loading).value.let { uiSurveyState ->
            when (uiSurveyState) {
                is UiState.Loading -> {
                    viewModel.getSurvey(uiLoginState?.accessToken.toString())
                }
                is UiState.Success -> {
                    MainSection(
                        userMood = uiSurveyState.data.data.mood,
                        userBudget = uiSurveyState.data.data.budget,
                        userDestinationCity = uiSurveyState.data.data.destinationCity,
                        navigateToSurvey = navigateToSurvey,
                        navigateToDetail = navigateToDetail,
                        sharedViewModel = sharedViewModel
                    )
                }
                is UiState.Error -> {}
            }
        }
    }
}

@Composable
fun TopSection(
    navigateToProfile: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        viewModel.getLogin()

        val hour by remember {
            mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
        }
        val uiLoginState by viewModel.uiLoginState.collectAsState()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            GifImage(gifImage = getPeriodSky(hour))
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.good)} ${stringResource(getPeriod(hour))},",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                )
                viewModel.uiUserState.collectAsState(
                    initial = UiState.Loading
                ).value.let { uiUserState ->
                    when (uiUserState) {
                        is UiState.Loading -> {
                            viewModel.getUser(accessToken = uiLoginState?.accessToken.toString())
                        }
                        is UiState.Success -> {
                            Text(
                                text = uiUserState.data.data.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        is UiState.Error -> {}
                    }
                }
            }
        }
        IconButton(
            onClick = { navigateToProfile() }
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.account_image),
                modifier = Modifier
                    .size(100.dp)
            )
        }
    }
}

@Composable
fun MainSection(
    userMood: String? = null,
    userBudget: String? = null,
    userDestinationCity: String? = null,
    navigateToSurvey: () -> Unit,
    navigateToDetail: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
    sharedViewModel: SharedViewModel
) {
    viewModel.getLogin()

    val uiLoginState by viewModel.uiLoginState.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    if (isLoading) {
        LoadingBar()
    }

    viewModel.uiTouristAttractionsState.collectAsState(
        initial = UiState.Loading
    ).value.let { uiTouristAttractionsState ->
        when (uiTouristAttractionsState) {
            is UiState.Loading -> {
                viewModel.getAllTouristAttractions(
                    uiLoginState?.accessToken.toString(),
                    userMood!!,
                    userBudget!!,
                    userDestinationCity!!
                )
            }
            is UiState.Success -> {
                isLoading = false
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    header {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(getMoodEmoji(userMood!!)),
                                            contentDescription = stringResource(R.string.mood_icon),
                                            modifier = Modifier
                                                .size(20.dp)
                                        )
                                        Text(
                                            text = stringResource(getMoodString(userMood)),
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                    Row {
                                        val showDialog = remember {
                                            mutableStateOf(false)
                                        }
                                        if (showDialog.value) {
                                            Alert(title = stringResource(
                                                R.string.change_mood),
                                                name = stringResource(R.string.alert_name),
                                                showDialog = showDialog.value,
                                                onConfirm = { navigateToSurvey() },
                                                onDismiss = { showDialog.value = false }
                                            )
                                        }

                                        IconButton(
                                            onClick = { showDialog.value = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RestartAlt,
                                                contentDescription = stringResource(R.string.restart)
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = stringResource(
                                    R.string.result_message
                                ),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                letterSpacing = .25.sp
                            )
                            Text(
                                text = stringResource(R.string.top_recommendation),
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                                letterSpacing = .15.sp
                            )
                        }
                    }
                    items(uiTouristAttractionsState.data.data) { touristAttraction ->
                        TouristAttractionItem(
                            image = R.drawable.jetpack_compose,
                            name = touristAttraction.name,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    sharedViewModel.selectedTouristAttractionItem = touristAttraction
                                    navigateToDetail()
                                }
                        )
                    }
                }
            }
            is UiState.Error -> {}
        }
    }
}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(
        span = { GridItemSpan(this.maxLineSpan) },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    JetViVeTheme {
        HomeContent(
            sharedViewModel = SharedViewModel(),
            navigateToProfile = {},
            navigateToSurvey = {},
            navigateToDetail = {}
        )
    }
}