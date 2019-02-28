package com.cse438.project2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import com.cse438.project2.model.Artist
import com.cse438.project2.util.QueryUtils

class ArtistViewModel(application: Application) : AndroidViewModel(application) {
    private var _artistsList: MutableLiveData<ArrayList<Artist>> = MutableLiveData()

    private var apiKey = "9712fd97a0f152aca00fac9b45107106"

    fun getArtistsByQueryText(query: String): MutableLiveData<ArrayList<Artist>> {
        loadArtist("?method=artist.search&artist=$query&api_key=$apiKey&format=json")
        return _artistsList
    }

    fun getSimilarArtistsByQueryText(artist: String): MutableLiveData<ArrayList<Artist>> {
        loadSimilarArtist("?method=artist.getsimilar&artist=$artist&api_key=$apiKey&format=json")
        return _artistsList
    }

    private fun loadSimilarArtist(query: String) {
        SimilarArtistAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class SimilarArtistAsyncTask : AsyncTask<String, Unit, ArrayList<Artist>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Artist>? {
            return QueryUtils.fetchSimilarArtistData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<Artist>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            } else {
                Log.e("RESULTS", result.toString())
                _artistsList.value = result
            }
        }
    }


    private fun loadArtist(query: String) {
        ArtistAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class ArtistAsyncTask : AsyncTask<String, Unit, ArrayList<Artist>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Artist>? {
            return QueryUtils.fetchArtistData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<Artist>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            } else {
                Log.e("RESULTS", result.toString())
                _artistsList.value = result
            }
        }
    }
}