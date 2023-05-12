package ie.wit.healthapp.ui.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.res.Configuration
import android.content.res.Resources
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.healthapp.R
import ie.wit.healthapp.models.ActivityModel
import ie.wit.healthapp.ui.auth.LoggedInViewModel
import ie.wit.healthapp.ui.report.ReportViewModel
import ie.wit.healthapp.utils.createLoader
import ie.wit.healthapp.utils.hideLoader
import ie.wit.healthapp.utils.showLoader

class MapsFragment : Fragment() {

    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    lateinit var loader : AlertDialog

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mapsViewModel.map = googleMap

        // Set the map style based on the current device theme.
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val styleResId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            R.raw.map_style_dark
        } else {
            R.raw.map_style_default
        }
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), styleResId
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        mapsViewModel.map.isMyLocationEnabled = true
        mapsViewModel.currentLocation.observe(viewLifecycleOwner) {
            val loc = LatLng(
                mapsViewModel.currentLocation.value!!.latitude,
                mapsViewModel.currentLocation.value!!.longitude
            )

            mapsViewModel.map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
            mapsViewModel.map.uiSettings.isZoomControlsEnabled = true
            mapsViewModel.map.uiSettings.isMyLocationButtonEnabled = true

            reportViewModel.observableActivitiesList.observe(
                viewLifecycleOwner
            ) { activities ->
                activities?.let {
                    render(activities as ArrayList<ActivityModel>)
                    hideLoader(loader)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loader = createLoader(requireActivity())
        setupMenu()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun render(activitiesList: ArrayList<ActivityModel>) {
        var markerColour: Float
        if (activitiesList.isNotEmpty()) {
            mapsViewModel.map.clear()
            activitiesList.forEach {
                markerColour = if(it.email.equals(this.reportViewModel.liveFirebaseUser.value!!.email))
                    BitmapDescriptorFactory.HUE_AZURE + 5
                else
                    BitmapDescriptorFactory.HUE_RED

                mapsViewModel.map.addMarker(
                    MarkerOptions().position(LatLng(it.latitude, it.longitude))
                        .title("${it.email} completed ${it.activityType} ${it.duration} mins")
                        .snippet(it.message)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColour ))
                )
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_report, menu)

                val item = menu.findItem(R.id.toggleActivities) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val toggleActivities: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                toggleActivities.isChecked = false

                toggleActivities.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) reportViewModel.loadAll()
                    else reportViewModel.loadOnMap()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Activities")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner) {
                firebaseUser -> if (firebaseUser != null) {
                reportViewModel.liveFirebaseUser.value = firebaseUser
                reportViewModel.loadOnMap()
            }
        }
    }
}