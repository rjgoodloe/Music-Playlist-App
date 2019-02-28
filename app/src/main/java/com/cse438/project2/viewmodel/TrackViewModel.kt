package com.cse438.project2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Log
import com.cse438.project2.model.Track
import com.cse438.project2.util.QueryUtils

class TrackViewModel(application: Application) : AndroidViewModel(application) {
    private var _tracksList: MutableLiveData<ArrayList<Track>> = MutableLiveData()


    private var apiKey = "9712fd97a0f152aca00fac9b45107106"

    fun getTopTracks(): MutableLiveData<ArrayList<Track>> {
        loadTracks("?method=chart.gettoptracks&api_key=$apiKey&format=json")
        return _tracksList
    }

    fun getTracksByQueryText(query: String): MutableLiveData<ArrayList<Track>> {
        loadTracksByArtist("?method=artist.gettoptracks&artist=$query&api_key=$apiKey&format=json")
        return _tracksList
    }

    private fun loadTracksByArtist(query: String) {
        TracksByArtistAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class TracksByArtistAsyncTask : AsyncTask<String, Unit, ArrayList<Track>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Track>? {
            return QueryUtils.fetchTracksByArtistData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            } else {
                Log.e("RESULTS", result.toString())
                _tracksList.value = result
            }
        }
    }

    private fun loadTracks(query: String) {
        TrackAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class TrackAsyncTask : AsyncTask<String, Unit, ArrayList<Track>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Track>? {
            return QueryUtils.fetchTopTrackData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            } else {
                Log.e("RESULTS", result.toString())
                _tracksList.value = result
            }
        }
    }

    fun getSimilarTracksByQueryText(artist: String, track: String): MutableLiveData<ArrayList<Track>> {
        loadSimilarTracks("?method=track.getsimilar&artist=$artist&track=$track&api_key=$apiKey&format=json")
        return _tracksList
    }

    private fun loadSimilarTracks(query: String) {
        SimilarTracksAsyncTask().execute(query)
    }

    @SuppressLint("StaticFieldLeak")
    inner class SimilarTracksAsyncTask : AsyncTask<String, Unit, ArrayList<Track>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Track>? {
            return QueryUtils.fetchSimilarTracksData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            } else {
                Log.e("RESULTS", result.toString())
                _tracksList.value = result
            }
        }
    }
}