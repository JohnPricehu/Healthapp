package ie.wit.healthapp.ui.report

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.healthapp.firebase.FirebaseDBManager
import ie.wit.healthapp.models.ActivityModel
import timber.log.Timber
import java.lang.Exception

class ReportViewModel : ViewModel() {

    private val activitiesList =
        MutableLiveData<List<ActivityModel>>()
    var readOnly = MutableLiveData(false)

    val observableActivitiesList: LiveData<List<ActivityModel>>
        get() = activitiesList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            //DonationManager.findAll(liveFirebaseUser.value?.email!!, activitiesList)
            readOnly.value = false
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!,activitiesList)
            Timber.i("Report Load Success : ${activitiesList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report Load Error : $e.message")
        }
    }

    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseDBManager.findAll(activitiesList)
            Timber.i("Report LoadAll Success : ${activitiesList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report LoadAll Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            //DonationManager.delete(userid,id)
            FirebaseDBManager.delete(userid,id)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }
}
