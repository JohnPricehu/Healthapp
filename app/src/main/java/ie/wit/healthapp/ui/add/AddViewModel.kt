package ie.wit.healthapp.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.healthapp.firebase.FirebaseDBManager
import ie.wit.healthapp.firebase.FirebaseImageManager
import ie.wit.healthapp.models.ActivityModel

class AddViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addActivity(firebaseUser: MutableLiveData<FirebaseUser>,
                    activity: ActivityModel) {
        status.value = try {
            // You should implement createActivity() method in FirebaseDBManager to add activity
            activity.profilepic = FirebaseImageManager.imageUri.value.toString()
            FirebaseDBManager.create(firebaseUser, activity)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
