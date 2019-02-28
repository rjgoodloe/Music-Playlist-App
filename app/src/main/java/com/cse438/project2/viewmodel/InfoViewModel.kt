package com.cse438.project2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import com.cse438.project2.model.Track
import com.cse438.project2.util.QueryUtils

class InfoViewModel(application: Application) : AndroidViewModel(application) {
    private var _details: MutableLiveData<Track> = MutableLiveData()

    private var apiKey = "9712fd97a0f152aca00fac9b45107106"

    fun getTrackInfoByQueryText(queryTitle: String, queryArtist: String): MutableLiveData<Track> {
        loadInfo("?method=track.getInfo&api_key=$apiKey&artist=$queryArtist&track=$queryTitle&format=json")
        return _details
    }

    private fun loadInfo(query: String) {
        InfoAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class InfoAsyncTask : AsyncTask<String, Unit, Track>() {
        override fun doInBackground(vararg params: String?): Track? {
            return QueryUtils.fetchTrackInfoData(params[0]!!)
        }

        override fun onPostExecute(result: Track?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            } else {
                Log.e("RESULTS", result.toString())
                _details.value = result
            }
        }
    }
}