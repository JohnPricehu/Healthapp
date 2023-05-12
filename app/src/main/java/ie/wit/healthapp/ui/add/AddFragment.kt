package ie.wit.healthapp.ui.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.healthapp.R
import ie.wit.healthapp.databinding.FragmentAddBinding
import ie.wit.healthapp.models.ActivityModel
import ie.wit.healthapp.ui.auth.LoggedInViewModel
import ie.wit.healthapp.ui.map.MapsViewModel
import ie.wit.healthapp.ui.report.ReportViewModel
import timber.log.Timber

class AddFragment : Fragment() {

    var totalAdded = 0
    private var _fragBinding: FragmentAddBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var addViewModel: AddViewModel
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentAddBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        addViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        fragBinding.progressBar.max = 10000
        fragBinding.durationPicker.minValue = 1
        fragBinding.durationPicker.maxValue = 1000

        fragBinding.durationPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to activityDuration
            fragBinding.activityDuration.setText("$newVal")
        }
        setButtonListener(fragBinding)

        return root;
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Report
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.activityError),Toast.LENGTH_LONG).show()
        }
    }

    fun setButtonListener(layout: FragmentAddBinding) {
        layout.addButton.setOnClickListener {
            val duration = if (layout.activityDuration.text.isNotEmpty())
                layout.activityDuration.text.toString().toInt() else layout.durationPicker.value
            val message = layout.activityMessage.text.toString()
            if(totalAdded >= layout.progressBar.max)
                Toast.makeText(context,"Add duration Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val activityType = when(layout.activityType.checkedRadioButtonId) {
                    R.id.Run -> "Run"
                    R.id.Swim -> "Swim"
                    R.id.Yoga -> "Yoga"
                    R.id.Basketball -> "Basketball"
                    else -> ""
                }
                totalAdded += duration
                layout.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalAdded)
                layout.progressBar.progress = totalAdded
                addViewModel.addActivity(loggedInViewModel.liveFirebaseUser,
                    ActivityModel(activityType = activityType,duration = duration,message = message,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!,
                        latitude = mapsViewModel.currentLocation.value!!.latitude,
                        longitude = mapsViewModel.currentLocation.value!!.longitude))
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_add, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        totalAdded = reportViewModel.observableActivitiesList.value!!.sumOf { it.duration }
        fragBinding.progressBar.progress = totalAdded
        fragBinding.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalAdded)
    }
}