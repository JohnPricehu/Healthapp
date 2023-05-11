package ie.wit.healthapp.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.healthapp.models.ActivityModel
import ie.wit.healthapp.models.ActivityStore
import timber.log.Timber

object FirebaseDBManager : ActivityStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(activitiesList: MutableLiveData<List<ActivityModel>>) {
        database.child("activities")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Activity error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ActivityModel>()
                    val children = snapshot.children
                    children.forEach {
                        val activity = it.getValue(ActivityModel::class.java)
                        localList.add(activity!!)
                    }
                    database.child("activities")
                        .removeEventListener(this)

                    activitiesList.value = localList
                }
            })
    }

    override fun findAll(userid: String, activitiesList: MutableLiveData<List<ActivityModel>>) {

        database.child("user-activities").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Activity error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ActivityModel>()
                    val children = snapshot.children
                    children.forEach {
                        val activity = it.getValue(ActivityModel::class.java)
                        localList.add(activity!!)
                    }
                    database.child("user-activities").child(userid)
                        .removeEventListener(this)

                    activitiesList.value = localList
                }
            })
    }

    override fun findById(userid: String, activityid: String, activity: MutableLiveData<ActivityModel>) {

        database.child("user-activities").child(userid)
            .child(activityid).get().addOnSuccessListener {
                activity.value = it.getValue(ActivityModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener {
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, activity: ActivityModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("activities").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        activity.uid = key
        val activityValues = activity.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/activities/$key"] = activityValues
        childAdd["/user-activities/$uid/$key"] = activityValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, activityid: String) {

        val childDelete: MutableMap<String, Any?> = HashMap()
        childDelete["/activities/$activityid"] = null
        childDelete["/user-activities/$userid/$activityid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, activityid: String, activity: ActivityModel) {

        val activityValues = activity.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["activities/$activityid"] = activityValues
        childUpdate["user-activities/$userid/$activityid"] = activityValues

        database.updateChildren(childUpdate)
    }

    fun updateImageRef(userid: String,imageUri: String) {

        val userActivities = database.child("user-activities").child(userid)
        val allActivities = database.child("activities")

        userActivities.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("profilepic").setValue(imageUri)
                        //Update all activities that match 'it'
                        val activity = it.getValue(ActivityModel::class.java)
                        allActivities.child(activity!!.uid!!)
                            .child("profilepic").setValue(imageUri)
                    }
                }
            })
    }
}


