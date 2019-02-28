package com.cse438.project2.database

import android.provider.BaseColumns


// Structural info about the database
class DbSettings {
    companion object {
        const val DB_NAME = "playlist.db"
        const val DB_VERSION = 5
    }

    // entry information
    class DbSongEntry: BaseColumns {
        companion object {
            const val TABLE = "playlist"
            const val ID = BaseColumns._ID
            const val COL_TITLE = "title"
            const val COL_URL = "url"
            const val COL_LISTENERS = "listeners"
            const val COL_PLAYCOUNT = "playcount"
            const val COL_ARTIST = "artist"
            const val COL_IMAGE = "images"
            const val COL_ALBUM = "album"

        }
    }
}