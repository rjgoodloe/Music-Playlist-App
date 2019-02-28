package com.cse438.project2.model

import java.io.Serializable


// holds information about artists
class Artist() : Serializable {

    private var name: String = ""
    private var listeners: String = ""
    private var url: String = ""
    private var images:  ArrayList<String> = ArrayList()


    constructor(

        name: String,
        listeners: String,
        url: String,
        images: ArrayList<String>

    ) : this() {
        this.name = name
        this.listeners = listeners
        this.url = url
        this.images = images


    }
    fun getName(): String {
        return this.name
    }
    fun getNumListeners(): String {
        return this.listeners
    }
    fun getURL(): String {
        return this.url
    }
    fun getImages(): ArrayList<String> {
        return this.images
    }

}
