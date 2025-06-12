

import kotlinx.serialization.Serializable

@Serializable
data class Pose3d(
    val lat: Double,
    val lon: Double,
    val alt: Int,
    val track: Double,
    val speed: Double,
    val vertRate: Double,
)

@Serializable
data class Aircraft(
    val identifier: String,
    val callsign: String,
    val pose3d: Pose3d,
)
