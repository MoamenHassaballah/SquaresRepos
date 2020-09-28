package com.moaapps.squaresrepos.ui.activities

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.moaapps.squaresrepos.R
import com.moaapps.squaresrepos.background.GetDataWorker
import com.moaapps.squaresrepos.pojo.Repo
import com.moaapps.squaresrepos.ui.adapters.ReposAdapter
import com.moaapps.squaresrepos.ui.interfaces.OnRepoClickedListener
import com.moaapps.squaresrepos.utils.InternetConnection.hasNetwork
import com.moaapps.squaresrepos.utils.Response
import com.moaapps.squaresrepos.viewmodels.GetReposViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import okhttp3.Cache
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnRepoClickedListener {
    private val reposAdapter = ReposAdapter(this)
    private var page = 1
    private var isLoading = true
    companion object{
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //init view model
        val getRepos = ViewModelProvider(this).get(GetReposViewModel::class.java)
        getRepos.repos.observe(this, {
            when (it.response) {
                Response.LOADING -> {
                    loading.visibility = View.VISIBLE
                    isLoading = true
                }
                Response.ERROR -> {
                    loadDone()
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                }
                Response.SUCCESS -> {
                    loadDone()
                    swipe_to_refresh.visibility = View.VISIBLE
                    reposAdapter.add(it.data!!)
                    page++
                }
            }
        })


        //setup recyclerview
        repos_rv.layoutManager = LinearLayoutManager(this)
        repos_rv.adapter = reposAdapter
        repos_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!repos_rv.canScrollVertically(1) && !isLoading) {
                    Log.d(TAG, "onScrollStateChanged: $page")
                    getRepos.get(this@MainActivity, page)
                }
            }
        })
        //getReposData
        getRepos.get(this,page)

        //Setup swipe to refresh
        swipe_to_refresh.setOnRefreshListener {
            clearCache()
            page = 1
            reposAdapter.empty()
            getRepos.get(this,1)
        }

        //set schedule work manager
        startWorkManager()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                reposAdapter.filter(newText!!)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    private fun clearCache() {
        if (hasNetwork(this)!!){
            val cashSize = (5 * 1024 * 1024).toLong()
            val myCache = Cache(cacheDir, cashSize)
            myCache.delete()
        }
    }

    private fun loadDone() {
        loading.visibility = View.GONE
        swipe_to_refresh.isRefreshing = false
        isLoading = false
    }

    override fun onLongClick(repo: Repo) {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
        view.go_to_repo.setOnClickListener {
            openUrl(repo.repoUrl)
            dialog.dismiss()
        }

        view.go_to_owner.setOnClickListener {
            openUrl(repo.ownerUrl)
            dialog.dismiss()
        }

        view.cancel.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun openUrl(url:String){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun startWorkManager(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val worker = PeriodicWorkRequestBuilder<GetDataWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork("work",
            ExistingPeriodicWorkPolicy.KEEP
            ,worker)
    }



}