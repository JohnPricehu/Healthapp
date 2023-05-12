import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.wit.healthapp.R
import ie.wit.healthapp.ui.news.NewsArticle

class NewsAdapter(private val articles: List<NewsArticle>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage: ImageView = itemView.findViewById(R.id.article_image)
        val articleTitle: TextView = itemView.findViewById(R.id.article_title)
        val articlePublishTime: TextView = itemView.findViewById(R.id.article_publish_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]

        // Assuming that `article.imageUrl`, `article.title`, and `article.publishTime` exist in your data model
        if (article.urlToImage.isNullOrEmpty()) {
            holder.articleImage.visibility = View.GONE
        } else {
            Picasso.get()
                .load(article.urlToImage)
                .error(R.drawable.ic_menu_share)  // replace with your own error image
                .into(holder.articleImage)
        }
        holder.articleTitle.text = article.title
        holder.articleTitle.setOnClickListener {
            if (article.url.isNotEmpty()) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                it.context.startActivity(browserIntent)
            }
        }
        holder.articlePublishTime.text = article.publishedAt
    }

    override fun getItemCount() = articles.size
}
