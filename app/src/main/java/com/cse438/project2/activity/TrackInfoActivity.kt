package com.cse438.project2.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.cse438.project2.R
import com.cse438.project2.database.PlaylistDatabaseHelper
import com.cse438.project2.model.Track
import com.cse438.project2.viewmodel.InfoViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_track_info.*

class TrackInfoActivity : AppCompatActivity() {

    private lateinit var track: Track

    private lateinit var viewModel: InfoViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_info)

        // get track from intent
        track = intent.extras!!.getSerializable("TRACK") as Track

        // add track to db
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            PlaylistDatabaseHelper(this).addSong(track)
            Snackbar.make(view, "${track.getTitle()} added to playlist", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    // update UI
    @SuppressLint("SetTextI18n")
    private fun loadUI(trackDetails: Track) {

        // special case:
        // song does not belong to album
        // use artist image and display "Single"
        if (trackDetails.getAlbum() == "Single") {
            val trackImages = track.getImages()
            val index = trackImages.size - 1
            if (trackImages.size == 0 || trackImages[index].isEmpty()) {
                // Do nothing
                // Images Array is either empty, or
                // the urls lead nowhere
            } else {

                // load, format, round corners of image
                Picasso.with(this)
                    .load(trackImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(900, 900)
                    .into(cover_img)
            }
        } else {

            // song is part of album
            // display album cover and album name
            val trackImages = trackDetails.getImages()
            val index = trackImages.size - 1
            if (trackImages.size == 0 || trackImages[index].isEmpty()) {
                // Do nothing
                // Images Array is either empty, or
                // the urls lead nowhere
            } else {

                // load, format, round corners of image
                Picasso.with(this)
                    .load(trackImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(900, 900)
                    .into(cover_img)
            }
        }

        // display text
        track_title.text = trackDetails.getTitle()
        track_artist.text = trackDetails.getArtist()
        track_album.text = trackDetails.getAlbum()
        track_play_count.text = "Play Count: ${trackDetails.getPlayCount()} "
        track_listeners.text = "Listeners: ${trackDetails.getNumListeners()} "

        track_artist.setOnClickListener {
            val data = Intent(this, MainActivity::class.java)
            data.putExtra("ARTIST", trackDetails)
            setResult(RESULT_OK,data)
            finish()}

        track_title.setOnClickListener {
            val data = Intent(this, MainActivity::class.java)
            data.putExtra("TITLE", trackDetails)
            setResult(Activity.RESULT_OK,data)
            finish()}
    }


    override fun onStart() {
        super.onStart()
        viewModel = ViewModelProviders.of(this).get(InfoViewModel::class.java)

        // observe updates as they arrive from async call
        val observer = android.arch.lifecycle.Observer<Track> {
            loadUI(it!!)
        }
        viewModel.getTrackInfoByQueryText(track.getTitle(), track.getArtist()).observe(this, observer)

    }
}
