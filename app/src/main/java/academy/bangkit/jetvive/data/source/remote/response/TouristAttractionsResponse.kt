package academy.bangkit.jetvive.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class TouristAttractionsResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: List<TouristAttractionData>
)

data class TouristAttractionData(
    @field:SerializedName("Place_Id")
    val id: Int,

    @field:SerializedName("Place_Name")
    val name: String,

    @field:SerializedName("City")
    val city: String,

    @field:SerializedName("Description")
    val description: String,

    @field:SerializedName("Rating")
    val rating: Double,

    @field:SerializedName("Lat")
    val lat: Double,

    @field:SerializedName("Long")
    val lon: Double
)