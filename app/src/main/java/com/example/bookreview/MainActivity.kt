package com.example.bookreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookreview.adapter.BookAdapter
import com.example.bookreview.api.BookService
import com.example.bookreview.databinding.ActivityMainBinding
import com.example.bookreview.model.BestSellerDto
import com.example.bookreview.model.SearchBooksDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var bookService: BookService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookRecyclerView()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSeller(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d(TAG, "isSuccessful not")
                        return
                    }

                    Log.d(TAG, "isSuccessful")

                    response.body()?.let {
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }

                        adapter.submitList(it.books)
                    }
                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    Log.d(TAG, "onFailure")
                }

            })

        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun search(keyword : String) {
        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBooksDto> {
                override fun onResponse(
                    call: Call<SearchBooksDto>,
                    response: Response<SearchBooksDto>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.d(TAG, "isSuccessful not")
                        return
                    }

                    adapter.submitList(response.body()?.books.orEmpty())
                }

                override fun onFailure(call: Call<SearchBooksDto>, t: Throwable) {
                    Log.d(TAG, "onFailure")
                }

            })
    }

    private fun initBookRecyclerView() {
        adapter = BookAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

}
