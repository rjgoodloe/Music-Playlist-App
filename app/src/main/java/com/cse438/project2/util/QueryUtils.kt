package com.cse438.project2.util

import android.text.TextUtils
import android.util.Log
import com.cse438.project2.model.Artist
import com.cse438.project2.model.Track
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset


class QueryUtils {
    companion object {
        private val LogTag = this::class.java.simpleName
        private const val BaseURL =
            "http://ws.audioscrobbler.com/2.0/" // localhost URL


        fun fetchTopTrackData(jsonQueryString: String): ArrayList<Track>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }
            return extractTopTracksFromJson(jsonResponse)
        }

        fun fetchTracksByArtistData(jsonQueryString: String): ArrayList<Track>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractTracksByArtistFromJson(jsonResponse)
        }

        fun fetchArtistData(jsonQueryString: String): ArrayList<Artist>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractArtistsFromJson(jsonResponse)
        }

        fun fetchSimilarArtistData(jsonQueryString: String): ArrayList<Artist>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractSimilarArtistsFromJson(jsonResponse)
        }

        fun fetchSimilarTracksData(jsonQueryString: String): ArrayList<Track>? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }

            return extractSimilarTracksFromJson(jsonResponse)
        }

        fun fetchTrackInfoData(jsonQueryString: String): Track? {
            val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem making the HTTP request.", e)
            }
            return extractTrackInfoFromJson(jsonResponse)
        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            } catch (e: MalformedURLException) {
                Log.e(this.LogTag, "Problem building the URL.", e)
            }

            return url
        }

        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if (url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000 // 10 seconds
                urlConnection.connectTimeout = 15000 // 15 seconds
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                } else {
                    Log.e(this.LogTag, "Error response code: ${urlConnection.responseCode}")
                }
            } catch (e: IOException) {
                Log.e(this.LogTag, "Problem retrieving the product data results: $url", e)
            } finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }

            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }

        private fun extractTopTracksFromJson(TrackJson: String?): ArrayList<Track>? {
            if (TextUtils.isEmpty(TrackJson)) {
                return null
            }

            val trackList = ArrayList<Track>()
            try {

                // tracks array
                val trackObject = JSONObject(TrackJson)
                val tracks = trackObject.getJSONObject("tracks")
                val trackArray = tracks.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val thisTrack = trackArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(thisTrack, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            val thisImage = images.getJSONObject(j)
                            imageArrayList.add(returnValueOrDefault<String>(thisImage, "#text") as String)
                        }
                    }

                    // Artist
                    val artist = thisTrack.getJSONObject("artist")
                    val artistName = returnValueOrDefault<String>(artist, "name") as String

                    trackList.add(
                        Track(
                            returnValueOrDefault<String>(thisTrack, "name") as String,
                            returnValueOrDefault<String>(thisTrack, "playcount") as String,
                            returnValueOrDefault<String>(thisTrack, "listeners") as String,
                            returnValueOrDefault<String>(thisTrack, "url") as String,
                            artistName,
                            imageArrayList,
                            ""
                        )
                    )

                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return trackList
        }

        private fun extractTracksByArtistFromJson(TrackJson: String?): ArrayList<Track>? {
            if (TextUtils.isEmpty(TrackJson)) {
                return null
            }

            val artistTrackList = ArrayList<Track>()
            try {


                val artistObject = JSONObject(TrackJson)
                val tracks = artistObject.getJSONObject("toptracks")
                val trackArray = tracks.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val thisTrack = trackArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(thisTrack, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            val thisImage = images.getJSONObject(j)
                            imageArrayList.add(returnValueOrDefault<String>(thisImage, "#text") as String)
                        }
                    }

                    // Artist
                    val artist = thisTrack.getJSONObject("artist")
                    val artistName = returnValueOrDefault<String>(artist, "name") as String



                    artistTrackList.add(
                        Track(
                            returnValueOrDefault<String>(thisTrack, "name") as String,
                            returnValueOrDefault<String>(thisTrack, "playcount") as String,
                            returnValueOrDefault<String>(thisTrack, "listeners") as String,
                            returnValueOrDefault<String>(thisTrack, "url") as String,
                            artistName,
                            imageArrayList,
                            ""
                        )
                    )
                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return artistTrackList
        }

        private fun extractArtistsFromJson(ArtistJson: String?): ArrayList<Artist>? {
            if (TextUtils.isEmpty(ArtistJson)) {
                return null
            }

            val artistList = ArrayList<Artist>()
            try {

                val artistObject = JSONObject(ArtistJson)
                val resultObject = artistObject.getJSONObject("results")
                val artistMatches = resultObject.getJSONObject("artistmatches")
                val artistArray = artistMatches.getJSONArray("artist")
                for (i in 0 until artistArray.length()) {
                    val thisArtist = artistArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(thisArtist, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            val thisImage = images.getJSONObject(j)
                            imageArrayList.add(returnValueOrDefault<String>(thisImage, "#text") as String)
                        }
                    }

                    artistList.add(
                        Artist(
                            returnValueOrDefault<String>(thisArtist, "name") as String,
                            returnValueOrDefault<String>(thisArtist, "listeners") as String,
                            returnValueOrDefault<String>(thisArtist, "url") as String,
                            imageArrayList
                        )
                    )

                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return artistList
        }

        private fun extractSimilarArtistsFromJson(ArtistJson: String?): ArrayList<Artist>? {
            if (TextUtils.isEmpty(ArtistJson)) {
                return null
            }

            val artistList = ArrayList<Artist>()
            try {

                val artistObject = JSONObject(ArtistJson)
                val similarArtist = artistObject.getJSONObject("similarartists")
                val artistArray = similarArtist.getJSONArray("artist")
                for (i in 0 until artistArray.length()) {
                    val thisArtist = artistArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(thisArtist, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            val thisImage = images.getJSONObject(j)
                            imageArrayList.add(returnValueOrDefault<String>(thisImage, "#text") as String)
                        }
                    }

                    artistList.add(
                        Artist(
                            returnValueOrDefault<String>(thisArtist, "name") as String,
                            "",
                            returnValueOrDefault<String>(thisArtist, "url") as String,
                            imageArrayList
                        )
                    )

                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return artistList
        }

        private fun extractSimilarTracksFromJson(TrackJson: String?): ArrayList<Track>? {
            if (TextUtils.isEmpty(TrackJson)) {
                return null
            }

            val trackList = ArrayList<Track>()
            try {

                val trackObject = JSONObject(TrackJson)
                val similarArtist = trackObject.getJSONObject("similartracks")
                val trackArray = similarArtist.getJSONArray("track")
                for (i in 0 until trackArray.length()) {
                    val thisTrack = trackArray.getJSONObject(i)

                    // Images
                    val images = returnValueOrDefault<JSONArray>(thisTrack, "image") as JSONArray?
                    val imageArrayList = ArrayList<String>()
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            val thisImage = images.getJSONObject(j)
                            imageArrayList.add(returnValueOrDefault<String>(thisImage, "#text") as String)
                        }
                    }

                    // Artist
                    val artist = thisTrack.getJSONObject("artist")
                    val artistName = returnValueOrDefault<String>(artist, "name") as String

                    trackList.add(
                        Track(
                            returnValueOrDefault<String>(thisTrack, "name") as String,
                            "",
                            "",
                            "",
                            artistName,
                            imageArrayList,
                            ""
                        )
                    )

                }

            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return trackList
        }

        private fun extractTrackInfoFromJson(infoJson: String?): Track? {
            if (TextUtils.isEmpty(infoJson)) {
                return null
            }
            val trackInfo: Track
            try {

                val infoObject = JSONObject(infoJson)
                val trackObject = infoObject.getJSONObject("track")
                var albumName = "Single"
                val imageArrayList = ArrayList<String>()
                if(trackObject.has("album")){
                    val albumObject = trackObject.getJSONObject("album")
                    albumName = returnValueOrDefault<String>(albumObject, "title") as String

                    // Images
                    val images = returnValueOrDefault<JSONArray>(albumObject, "image") as JSONArray?
                    if (images != null) {
                        for (j in 0 until images.length()) {
                            val thisImage = images.getJSONObject(j)
                            imageArrayList.add(returnValueOrDefault<String>(thisImage, "#text") as String)
                        }
                    }


                }
                val artistObject = trackObject.getJSONObject("artist")
                val artistName = returnValueOrDefault<String>(artistObject, "name") as String

                Log.e("artist: ", artistName)

                trackInfo = Track(
                    returnValueOrDefault<String>(trackObject, "name") as String,
                    returnValueOrDefault<String>(trackObject, "playcount") as String,
                    returnValueOrDefault<String>(trackObject, "listeners") as String,
                    returnValueOrDefault<String>(trackObject, "url") as String,
                    artistName,
                    imageArrayList,
                    albumName
                )


                Log.e("Correct ", trackInfo.toString())
                return trackInfo
            } catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }
            Log.e("Incorrect ", "Uh oh")

            return Track()
        }

        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        ""
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    } else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    } else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    } else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    } else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    } else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }

    }
}