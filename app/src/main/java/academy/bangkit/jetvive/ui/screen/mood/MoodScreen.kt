package academy.bangkit.jetvive.ui.screen.mood

import academy.bangkit.jetvive.R
import academy.bangkit.jetvive.helper.ViewModelFactory
import academy.bangkit.jetvive.model.mood.FakeMoodDataSource
import academy.bangkit.jetvive.model.mood.Mood
import academy.bangkit.jetvive.ui.common.UiState
import academy.bangkit.jetvive.ui.components.MoodItem
import academy.bangkit.jetvive.ui.components.WelcomeBar
import academy.bangkit.jetvive.ui.screen.home.HomeViewModel
import academy.bangkit.jetvive.ui.theme.JetViVeTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MoodScreen(
    navigateToSurvey: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
    modifier: Modifier = Modifier
) {
    viewModel.uiMoodState.collectAsState(initial = UiState.Loading).value.let { uiMoodState ->
        when (uiMoodState) {
            is UiState.Loading -> {
                viewModel.getAllMoods()
            }
            is UiState.Success -> {
                MoodContent(
                    moods = uiMoodState.data,
                    navigateToSurvey =  navigateToSurvey
                )
            }
            is UiState.Error -> {}
        }
    }
}

@Composable
fun MoodContent(
    moods: List<Mood>,
    navigateToSurvey: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .height(335.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(285.dp)
                    .clip(shape = RectangleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Text(
                        text = stringResource(R.string.halowisata),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        letterSpacing = .15.sp,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            color = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Image(
                        painter = painterResource(R.drawable.welcome),
                        contentDescription = stringResource(R.string.welcome_image)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .shadow(
                        8.dp,
                        shape = RoundedCornerShape(15.dp),
                        spotColor = MaterialTheme.colorScheme.onBackground
                    )
            ) {
                WelcomeBar(
                    name = "John Doe",
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(
                vertical = 30.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(moods) { mood ->
                MoodItem(
                    color = Color(mood.color),
                    image = mood.image,
                    text = mood.name,
                    onClick = { navigateToSurvey() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoodContentPreview() {
    JetViVeTheme {
        MoodContent(
            moods = FakeMoodDataSource.dummyMoods,
            navigateToSurvey = {}
        )
    }
}