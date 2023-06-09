package academy.bangkit.jetvive.main

import academy.bangkit.jetvive.R
import academy.bangkit.jetvive.helper.SharedViewModel
import academy.bangkit.jetvive.helper.TwiceBackPressExit
import academy.bangkit.jetvive.helper.ViewModelFactory
import academy.bangkit.jetvive.ui.components.BottomBar
import academy.bangkit.jetvive.ui.navigation.Screen
import academy.bangkit.jetvive.ui.screen.bookmark.BookmarkScreen
import academy.bangkit.jetvive.ui.screen.detail.DetailBookmarkScreen
import academy.bangkit.jetvive.ui.screen.detail.DetailHomeScreen
import academy.bangkit.jetvive.ui.screen.survey.SurveyScreen
import academy.bangkit.jetvive.ui.screen.home.HomeScreen
import academy.bangkit.jetvive.ui.screen.login.LoginScreen
import academy.bangkit.jetvive.ui.screen.onboarding.OnboardingScreen
import academy.bangkit.jetvive.ui.screen.profile.ProfileScreen
import academy.bangkit.jetvive.ui.screen.register.RegisterScreen
import academy.bangkit.jetvive.ui.theme.JetViVeTheme
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun JetViVeApp(
    navController: NavHostController = rememberAnimatedNavController(),
    viewModel: MainViewModel = viewModel(
        factory = ViewModelFactory.getInstance(context = LocalContext.current)
    ),
    sharedViewModel: SharedViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun launchSnackbar(
        message: String,
        actionLabel : String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ){
        scope.launch {
            snackState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = when (currentRoute) {
                    Screen.Home.route -> true
                    Screen.Bookmark.route -> true
                    else -> false
                },
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomBar(navController = navController)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackState)
        }
    ) { innerPadding ->
        viewModel.getLogin()

        val uiLoginState by viewModel.uiLoginState.collectAsState()

        val startDestination = remember {
            if (uiLoginState?.accessToken?.isNotBlank() == true) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }
        }

        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Onboarding.route,
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Login.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                OnboardingScreen(
                    navigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
                TwiceBackPressExit()
            }
            composable(
                route = Screen.Login.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Onboarding.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        Screen.Register.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Register.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        Screen.Survey.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                LoginScreen(
                    navigateToSignUp = {
                        navController.navigate(Screen.Register.route)
                    },
                    navigateToSurvey = {
                        navController.navigate(Screen.Survey.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    },
                    launchSnackbar = {
                        launchSnackbar(
                            message = context.getString(R.string.coming_soon),
                            actionLabel = context.getString(R.string.hide),
                            duration = SnackbarDuration.Short
                        )
                    }
                )
                TwiceBackPressExit()
            }
            composable(
                route = Screen.Register.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Login.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Login.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                RegisterScreen(
                    navigateToSignIn = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = Screen.Survey.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Login.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        Screen.Home.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Home.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                SurveyScreen(
                    navigateToHome = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }
            composable(
                route = Screen.Home.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Survey.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        Screen.Profile.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        Screen.Bookmark.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        Screen.DetailHomeTouristAttraction.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Profile.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        Screen.Survey.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        Screen.Bookmark.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        Screen.DetailHomeTouristAttraction.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                HomeScreen(
                    navigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    navigateToSurvey = {
                        navController.navigate(Screen.Survey.route)
                    },
                    navigateToDetail = {
                        navController.navigate(Screen.DetailHomeTouristAttraction.route)
                    },
                    sharedViewModel = sharedViewModel
                )
                TwiceBackPressExit()
            }
            composable(
                route = Screen.Bookmark.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Home.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        Screen.DetailHomeTouristAttraction.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Home.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Right,
                                animationSpec = tween(700)
                            )
                        Screen.DetailHomeTouristAttraction.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                BookmarkScreen(
                    sharedViewModel = sharedViewModel,
                    navigateToDetail = {
                        navController.navigate(Screen.DetailBookmarkTouristAttraction.route)
                    }
                )
            }
            composable(
                route = Screen.Profile.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Home.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Home.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
            ) {
                ProfileScreen(
                    onBackClick = {
                        navController.navigateUp()
                    },
                    navigateToSignIn = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    launchSnackbar = {
                        launchSnackbar(
                            message = context.getString(R.string.coming_soon),
                            actionLabel = context.getString(R.string.hide),
                            duration = SnackbarDuration.Short
                        )
                    }
                )
            }
            composable(
                route = Screen.DetailHomeTouristAttraction.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Home.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        Screen.Bookmark.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Home.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        Screen.Bookmark.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                }
            ) {
                DetailHomeScreen(
                    sharedViewModel = sharedViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }
            composable(
                route = Screen.DetailBookmarkTouristAttraction.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Screen.Home.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        Screen.Bookmark.route ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Screen.Home.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        Screen.Bookmark.route ->
                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Down,
                                animationSpec = tween(700)
                            )
                        else -> null
                    }
                }
            ) {
                DetailBookmarkScreen(
                    sharedViewModel = sharedViewModel,
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JetViVeAppPreview() {
    JetViVeTheme {
        JetViVeApp()
    }
}