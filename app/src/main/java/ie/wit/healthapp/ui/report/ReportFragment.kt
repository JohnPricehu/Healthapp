package ie.wit.healthapp.ui.report

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.healthapp.R
import ie.wit.healthapp.adapters.HealthAppAdapter
import ie.wit.healthapp.adapters.HealthAppClickListener
import ie.wit.healthapp.databinding.FragmentReportBinding
import ie.wit.healthapp.models.ActivityModel
import ie.wit.healthapp.ui.auth.LoggedInViewModel
import ie.wit.healthapp.utils.*

class ReportFragment : Fragment(), HealthAppClickListener {

    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader: AlertDialog
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentReportBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = ReportFragmentDirections.actionReportFragmentToActivityFragment()
            findNavController().navigate(action)
        }
        showLoader(loader, "Downloading Activities")
        reportViewModel.observableActivitiesList.observe(
            viewLifecycleOwner,
            Observer { activities ->
                activities?.let {
                    render(activities as ArrayList<ActivityModel>)
                    hideLoader(loader)
                    checkSwipeRefresh()
                }
            })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader, "Deleting Activity")
                val adapter = fragBinding.recyclerView.adapter as HealthAppAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                reportViewModel.delete(
                    reportViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as ActivityModel).uid!!
                )

                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onActivityClick(viewHolder.itemView.tag as ActivityModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
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
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(activitiesList: ArrayList<ActivityModel>) {
        fragBinding.recyclerView.adapter = HealthAppAdapter(activitiesList, this)
        if (activitiesList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.activitiesNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.activitiesNotFound.visibility = View.GONE
        }
    }

    override fun onActivityClick(activity: ActivityModel) {
        val action =
            ReportFragmentDirections.actionReportFragmentToActivityDetailFragment(activity.uid!!)
        findNavController().navigate(action)
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader, "Downloading Activities")
            reportViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Activities")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                reportViewModel.liveFirebaseUser.value = firebaseUser
                reportViewModel.load()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}

