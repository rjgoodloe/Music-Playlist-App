package com.cse438.project2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cse438.project2.R
import com.cse438.project2.activity.MainActivity
import com.cse438.project2.activity.TrackInfoActivity
import com.cse438.project2.model.Track
import com.cse438.project2.viewmodel.PlaylistViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.top_track_list_item.view.*


@SuppressLint("ValidFragment")
class PlaylistFragment(context: Context) : Fragment() {

    private val parentContext = context
    private var adapter = PlaylistAdapter()
    private lateinit var viewModel: PlaylistViewModel
    private var playlist: ArrayList<Track> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onStart() {
        super.onStart()

        // set up recycler view
        playlist_recycler_view.layoutManager = LinearLayoutManager(this.context)
        viewModel = ViewModelProviders.of(this).get(PlaylistViewModel::class.java)
        updateRecyclerView()

    }

    // observe updates to recyclerView as the come from async task
    fun updateRecyclerView(){
        val observer = Observer<ArrayList<Track>> {
            playlist_recycler_view.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    if (p0 >= playlist.size || p1 >= playlist.size) {
                        return false
                    }
                    return playlist[p0].getTitle() == playlist[p1].getTitle()
                }

                override fun getOldListSize(): Int {
                    return playlist.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return playlist[p0] == playlist[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            playlist = it ?: ArrayList()
        }



        viewModel.getPlaylist().observe(this, observer)
    }


    inner class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.TrackViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TrackViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.top_track_list_item, p0, false)
            return TrackViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: TrackViewHolder, p1: Int) {
            val track = playlist[p1]
            val trackImages = track.getImages()
            val index = trackImages.size - 1
            if (trackImages.size == 0 || trackImages[index].isEmpty()) {
                // Do nothing
                // Images Array is either empty, or
                // the urls lead nowhere
            } else {
                Picasso.with(this@PlaylistFragment.context)
                    .load(trackImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(500, 500)
                    .into(p0.trackImg)
            }
            p0.trackTitle.text = track.getTitle()

            // on track selected start trackInfoActivity
            p0.itemView.setOnClickListener {
                val intent = Intent(this@PlaylistFragment.parentContext, TrackInfoActivity::class.java)
                intent.putExtra("TRACK", track)
                (activity as MainActivity).startActivityForResult(intent, 0)
            }

            // when selected and held, confirm deletion from playlist
            p0.itemView.setOnLongClickListener {
                showDialog(track.getID())
                true
            }
        }

        private fun showDialog(id: String) {
            AlertDialog.Builder(this@PlaylistFragment.parentContext)
                .setTitle("Delete song")
                .setMessage("Are you sure you want to delete this song from your playlist?")

                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteFromPlaylist(id)
                    this@PlaylistFragment.updateRecyclerView()
                }

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Cancel", null)
                .show()
        }

        override fun getItemCount(): Int {
            return playlist.size
        }

        inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var trackImg: ImageView = itemView.cover_img
            var trackTitle: TextView = itemView.track_title
        }
    }
}
