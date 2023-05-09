package ie.wit.healthapp.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ActivityStore {
    fun findAll(activitiesList:
                MutableLiveData<List<ActivityModel>>)
    fun findAll(userId: String,
                activitiesList:
                MutableLiveData<List<ActivityModel>>)
    fun findById(userId: String, activityId: String,
                 activity: MutableLiveData<ActivityModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, activity: ActivityModel)
    fun delete(userId: String, activityId: String)
    fun update(userId: String, activityId: String, activity: ActivityModel)
}
