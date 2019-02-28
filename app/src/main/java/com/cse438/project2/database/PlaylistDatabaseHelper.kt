package com.cse438.project2.database


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cse438.project2.model.Track

class PlaylistDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {

    // set up the interaction with the database
    override fun onCreate(db: SQLiteDatabase?) {
        val createPlaylistTableQuery = "CREATE TABLE " + DbSettings.DbSongEntry.TABLE + " ( " +
                DbSettings.DbSongEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DbSongEntry.COL_TITLE + " TEXT NULL, " +
                DbSettings.DbSongEntry.COL_URL + " TEXT NULL, " +
                DbSettings.DbSongEntry.COL_LISTENERS + " TEXT NULL, " +
                DbSettings.DbSongEntry.COL_PLAYCOUNT + " TEXT NULL, " +
                DbSettings.DbSongEntry.COL_ARTIST + " TEXT NULL, " +
                DbSettings.DbSongEntry.COL_IMAGE + " TEXT NULL, " +
                DbSettings.DbSongEntry.COL_ALBUM + " TEXT NULL)"

        // will not create new database if one already exists
        db?.execSQL(createPlaylistTableQuery)
    }

    // upgrade triggers on new version number in dbsettings -> throws out old table and replaces with fresh one
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + DbSettings.DbSongEntry.TABLE)
        onCreate(db)
    }

    // add song to playlist database
    fun addSong(song: Track) {
        // make database writable
        val db = this.writableDatabase

        // Create a new map of values, with column names as keys
        val values = ContentValues().apply {
            put(DbSettings.DbSongEntry.COL_TITLE, song.getTitle())
            put(DbSettings.DbSongEntry.COL_URL, song.getURL())
            put(DbSettings.DbSongEntry.COL_LISTENERS, song.getNumListeners())
            put(DbSettings.DbSongEntry.COL_PLAYCOUNT, song.getPlayCount())
            put(DbSettings.DbSongEntry.COL_ARTIST, song.getArtist())
            put(DbSettings.DbSongEntry.COL_IMAGE, song.getImages()[song.getImages().size - 1])
            put(DbSettings.DbSongEntry.COL_ALBUM, song.getAlbum())
        }

        // new row ID
        db?.insert(DbSettings.DbSongEntry.TABLE, null, values)
    }


}