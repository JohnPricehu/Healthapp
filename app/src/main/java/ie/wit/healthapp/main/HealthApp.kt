package ie.wit.healthapp.main

import android.app.Application
import timber.log.Timber

class HealthApp : Application() {

    //lateinit var activitiesStore: ActivityStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        //activitiesStore = ActivityManager()
        Timber.i("HealthApp Application Started")
    }
}
