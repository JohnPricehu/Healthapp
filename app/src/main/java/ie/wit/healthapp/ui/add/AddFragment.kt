package ie.wit.healthapp.ui.activity

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ie.wit.healthapp.R
import ie.wit.healthapp.databinding.FragmentActivityBinding
import ie.wit.healthapp.models.ActivityModel
import ie.wit.healthapp.ui.auth.LoggedInViewModel
import ie.wit.healthapp.ui.add.AddViewModel
import ie.wit.healthapp.ui.report.ReportViewModel

class AddFragment : Fragment() {

    private var _fragBinding: FragmentActivityBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var addViewModel: AddViewModel
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentActivityBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        addViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        fragBinding.addActivityButton.setOnClickListener {
            val activityDescription = fragBinding.activityDescription.text.toString()
            if (activityDescription.isNotBlank()) {
                val activity = ActivityModel(description = activityDescription,
                    email = loggedInViewModel.liveFirebaseUser.value?.email!!)
                addViewModel.addActivity(loggedInViewModel.liveFirebaseUser, activity)
            } else {
                Toast.makeText(context, getString(R.string.activityError), Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Report
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context, getString(R.string.activityError), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}
