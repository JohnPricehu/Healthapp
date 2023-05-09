package ie.wit.healthapp.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ie.wit.healthapp.databinding.FragmentActivityDetailBinding
import ie.wit.healthapp.ui.auth.LoggedInViewModel
import ie.wit.healthapp.ui.report.ReportViewModel
import timber.log.Timber


class ActivityDetailFragment : Fragment() {

    private lateinit var detailViewModel: ActivityDetailViewModel
    private val args by navArgs<ActivityDetailFragmentArgs>()
    private var _fragBinding: FragmentActivityDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentActivityDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(ActivityDetailViewModel::class.java)
        detailViewModel.observableActivity.observe(viewLifecycleOwner, Observer { render() })

        fragBinding.editActivityButton.setOnClickListener {
            detailViewModel.updateActivity(loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.activityid, fragBinding.healthvm?.observableActivity!!.value!!)
            findNavController().navigateUp()
        }

        fragBinding.deleteActivityButton.setOnClickListener {
            reportViewModel.delete(loggedInViewModel.liveFirebaseUser.value?.email!!,
                detailViewModel.observableActivity.value?.uid!!)
            findNavController().navigateUp()
        }

        return root
    }

    private fun render() {
        fragBinding.editMessage.setText("A Message")
        fragBinding.editUpvotes.setText("0")
        fragBinding.healthvm = detailViewModel
        Timber.i("Retrofit fragBinding.activityvm == $fragBinding.activityvm")
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getActivity(loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.activityid)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}
