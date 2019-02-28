package com.cse438.project2.model

import java.io.Serializable

// holds information about tracks
class Track() : Serializable {

    private var id: String = ""
    private var title: String = ""
    private var url: String = ""
    private var listeners: String = ""
    private var playcount: String = ""
    private var artist: String = ""
    private var images:  ArrayList<String> = ArrayList()
    private var album: String = ""

    constructor(

        title: String,
        playcount: String,
        listeners: String,
        url: String,
        artist: String,
        images: ArrayList<String>,
        album: String
    ) : this() {
        this.title = title
        this.playcount = playcount
        this.listeners = listeners
        this.url = url
        this.artist = artist
        this.images = images
        this.album = album
    }

    fun getTitle(): String {
        return this.title
    }
    
    fun getPlayCount(): String {
        return this.playcount
    }
    fun getNumListeners(): String {
        return this.listeners
    }
    fun getURL(): String {
        return this.url
    }
    fun getArtist(): String {
        return this.artist
    }
    fun getImages(): ArrayList<String> {
        return this.images
    }
    fun getAlbum(): String {
        return this.album
    }
    fun getID(): String {
        return this.id
    }
    fun setID(ID: String){
        this.id = ID
    }
}
