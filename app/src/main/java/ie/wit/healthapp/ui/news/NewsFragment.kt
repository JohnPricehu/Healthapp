package ie.wit.healthapp.ui.news

import NewsAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import ie.wit.healthapp.R
import okhttp3.*
import java.io.IOException

class NewsFragment : Fragment() {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView
    private var selectedCountry = "ie"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        newsAdapter = NewsAdapter(emptyList())
        recyclerView.adapter = newsAdapter

        val countrySpinner = view.findViewById<Spinner>(R.id.countrySpinner)
        val countries = arrayOf("ie", "us", "gb", "de", "fr")  // Add more countries if you want
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCountry = countries[position]
                fetchNews()  // Fetch news again when the selected country changes
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        fetchNews()
    }

    private fun updateUI(articles: List<NewsArticle>) {
        newsAdapter = NewsAdapter(articles)
        recyclerView.adapter = newsAdapter
    }

    private fun fetchNews() {
        val url = "https://newsapi.org/v2/top-headlines?country=$selectedCountry&category=sports&apiKey=8ca5a5b0aadd4409a9005f71d9e220b5"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()

                    val gson = Gson()
                    val newsResponse = gson.fromJson(body, NewsResponse::class.java)

                    // Now you have the news data, you can update your UI
                    activity?.runOnUiThread {
                        updateUI(newsResponse.articles)
                    }
                }
            }
        })
    }
}
