package academy.bangkit.jetvive.data.source.remote.request

data class SurveyRequest(
    val moodId: String,
    val budget: String,
    val travelDistance: String,
    val destinationCity: String
)