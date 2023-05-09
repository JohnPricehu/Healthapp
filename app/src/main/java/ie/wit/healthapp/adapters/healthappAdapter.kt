package ie.wit.healthapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.healthapp.R
import ie.wit.healthapp.databinding.CardActivityBinding
import ie.wit.healthapp.models.ActivityModel

interface HealthAppClickListener {
    fun onActivityClick(activity: ActivityModel)
}

class HealthAppAdapter constructor(private var activities: ArrayList<ActivityModel>,
                                  private val listener: HealthAppClickListener)
    : RecyclerView.Adapter<HealthAppAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardActivityBinding
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

    inner class MainHolder(val binding: CardActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: ActivityModel, listener: HealthAppClickListener) {
            binding.root.tag = activity
            binding.activity = activity
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onActivityClick(activity) }
            binding.executePendingBindings()
        }
    }
}
