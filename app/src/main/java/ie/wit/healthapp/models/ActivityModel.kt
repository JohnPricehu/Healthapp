package ie.wit.healthapp.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ActivityModel(
      var uid: String? = "",
      var activityType: String = "N/A",
      var duration: Int = 0,
      var message: String = "a message",
      var upvotes: Int = 0,
      var profilepic: String = "",
      var latitude: Double = 0.0,
      var longitude: Double = 0.0,
      var email: String? = "joe@bloggs.com")
      : Parcelable
{
      @Exclude
      fun toMap(): Map<String, Any?> {
            return mapOf(
                  "uid" to uid,
                  "activityType" to activityType,
                  "duration" to duration,
                  "message" to message,
                  "upvotes" to upvotes,
                  "profilepic" to profilepic,
                  "latitude" to latitude,
                  "longitude" to longitude,
                  "email" to email
            )
      }
}
