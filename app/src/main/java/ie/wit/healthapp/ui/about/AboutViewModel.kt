package ie.wit.healthapp.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the About Fragment for the Health App"
    }
    val text: LiveData<String> = _text
}
