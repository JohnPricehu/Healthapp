package ie.wit.healthapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.healthapp.firebase.FirebaseDBManager
import ie.wit.healthapp.models.ActivityModel
import timber.log.Timber

class ActivityDetailViewModel : ViewModel() {
    private val activity = MutableLiveData<ActivityModel>()

    var observableActivity: LiveData<ActivityModel>
        get() = activity
        set(value) {activity.value = value.value}

    fun getActivity(userid: String, id: String) {
        try {
            //ActivityManager.findById(email, id, activity)
            FirebaseDBManager.findById(userid, id, activity)
            Timber.i("Detail getActivity() Success : ${
                activity.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getActivity() Error : $e.message")
        }
    }

    fun updateActivity(userid: String, id: String, activity: ActivityModel) {
        try {
            //ActivityManager.update(email, id, activity)
            FirebaseDBManager.update(userid, id, activity)
            Timber.i("Detail update() Success : $activity")
        }
        catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}
