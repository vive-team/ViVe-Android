package academy.bangkit.jetvive.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding: Screen("onboarding")
    object Login: Screen("login")
    object Register: Screen("register")
    object Survey: Screen("survey")
    object Home: Screen("home")
    object Bookmark: Screen("bookmark")
    object Profile: Screen("profile")
    object DetailHomeTouristAttraction: Screen("detailHomeTouristAttraction")
    object DetailBookmarkTouristAttraction: Screen("detailBookmarkTouristAttraction")
}