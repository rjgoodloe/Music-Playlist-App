package com.cse438.project2.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.provider.BaseColumns
import android.util.Log
import com.cse438.project2.database.DbSettings
import com.cse438.project2.database.PlaylistDatabaseHelper
import com.cse438.project2.model.Track


class PlaylistViewModel(application: Application) : AndroidViewModel(application) {
    private var _trackList: MutableLiveData<ArrayList<Track>> = MutableLiveData()
    private var _playlistDBHelper: PlaylistDatabaseHelper = PlaylistDatabaseHelper(application)

    fun getPlaylist(): MutableLiveData<ArrayList<Track>> {
        loadSavedTracks()
        return _trackList
    }

    private fun loadSavedTracks() {
        val db = _playlistDBHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            DbSettings.DbSongEntry.COL_TITLE,
            DbSettings.DbSongEntry.COL_URL,
            DbSettings.DbSongEntry.COL_LISTENERS,
            DbSettings.DbSongEntry.COL_PLAYCOUNT,
            DbSettings.DbSongEntry.COL_ARTIST,
            DbSettings.DbSongEntry.COL_IMAGE,
            DbSettings.DbSongEntry.COL_ALBUM

        )

        val cursor = db.query(
            DbSettings.DbSongEntry.TABLE,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )

        val savedTracks = ArrayList<Track>()
        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_TITLE))
                val url = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_URL))
                val listeners = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_LISTENERS))
                val playcount = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_PLAYCOUNT))
                val artist = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_ARTIST))
                val image = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_IMAGE))
                val album = getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.COL_ALBUM))
                val imageArrayList = arrayListOf(image)

                val thisTrack = Track(title, url, listeners, playcount, artist, imageArrayList, album)
                thisTrack.setID(getString(getColumnIndexOrThrow(DbSettings.DbSongEntry.ID)))
                savedTracks.add(thisTrack)
            }
        }
        _trackList.value = savedTracks
    }

    fun deleteFromPlaylist(id: String) {
        val db = _playlistDBHelper.writableDatabase
        db.delete("playlist", "_id = '$id'", null)
        db.close()

    }

}