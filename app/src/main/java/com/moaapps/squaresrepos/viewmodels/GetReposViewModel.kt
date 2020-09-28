package com.moaapps.squaresrepos.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.moaapps.squaresrepos.pojo.Repo
import com.moaapps.squaresrepos.utils.InitRetrofit
import com.moaapps.squaresrepos.utils.InternetConnection.hasNetwork
import com.moaapps.squaresrepos.utils.Resources
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetReposViewModel() : ViewModel() {
    companion object {
        private const val TAG = "GetReposViewModel"
    }

    val repos: MutableLiveData<Resources<List<Repo>>> = MutableLiveData()

    fun get(context:Context, page: Int) {
        repos.postValue(Resources.loading());
        GlobalScope.launch {
            val response = InitRetrofit(context).retrofit.getRepos(page, 10)
            if (response.isSuccessful) {
                val resString = Gson().toJson(response.body())
                val list = ArrayList<Repo>()
                val jsonArray = JSONArray(resString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val repoUrl = jsonObject.getString("html_url")
                    val description = try {
                        jsonObject.getString("description")
                    } catch (e: Exception) {
                        ""
                    }
                    val fork = try {
                        jsonObject.getBoolean("fork")
                    } catch (e: Exception) {
                        null
                    }
                    val owner = jsonObject.getJSONObject("owner").getString("login")
                    val ownerUrl = jsonObject.getJSONObject("owner").getString("html_url")

                    val repo = Repo(name, description, owner, repoUrl, ownerUrl, fork)
                    list.add(repo)
                }
                repos.postValue(Resources.success(list, ""))
            } else {
                Log.d(TAG, "onResponse Error: ${response.errorBody()?.string()}")
                repos.postValue(
                    Resources.error(
                        if (response.errorBody() != null && response.errorBody()!!.string().isNotEmpty()) {
                            response.errorBody()!!.string()
                        }else if(!hasNetwork(context)!!){
                            "No Internet Connection"
                        } else {
                            "An error occurred, Please try again later"
                        }
                    )
                )
            }
        }

    }
}