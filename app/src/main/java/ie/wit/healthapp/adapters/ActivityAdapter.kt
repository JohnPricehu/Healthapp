package ie.wit.healthapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.healthapp.R
import ie.wit.healthapp.databinding.CardHealthappBinding
import ie.wit.healthapp.models.ActivityModel
import ie.wit.healthapp.utils.customTransformation

interface ActivityClickListener {
    fun onActivityClick(activity: ActivityModel)
}

class ActivityAdapter constructor(private var activities: ArrayList<ActivityModel>,
                                   private val listener: ActivityClickListener,
                                   private val readOnly: Boolean)
    : RecyclerView.Adapter<ActivityAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardHealthappBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val activity = activities[holder.adapterPosition]
        holder.bind(activity, listener)
    }

    fun removeAt(position: Int) {
        activities.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = activities.size

    inner class MainHolder(val binding: CardHealthappBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        fun bind(activity: ActivityModel, listener: ActivityClickListener) {
            binding.root.tag = activity
            binding.activity = activity
            Picasso.get().load(activity.profilepic.toUri())
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .into(binding.imageIcon)
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onActivityClick(activity) }
            binding.executePendingBindings()
        }
    }
}
