package com.cse438.project2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
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
import com.cse438.project2.viewmodel.TrackViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.top_track_list_item.view.*



@SuppressLint("ValidFragment")
class TopTracksFragment(context: Context) : Fragment() {
    private var adapter = TrackAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: TrackViewModel
    private var trackList: ArrayList<Track> = ArrayList()
    private lateinit var topTrackList: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.fragment_top_tracks, container, false)
        topTrackList = view!!.findViewById(R.id.top_tracks_list)
        return view
    }

    override fun onStart() {
        super.onStart()

        // set up recyclerView as Grid Layout
        topTrackList.layoutManager = GridLayoutManager(parentContext, 2)
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        // observe updates to recyclerView as Top Tracks are received
        val observer = android.arch.lifecycle.Observer<ArrayList<Track>> {
            topTrackList.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return trackList[p0].getTitle() == trackList[p1].getTitle()
                }

                override fun getOldListSize(): Int {
                    return trackList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return trackList[p0] == trackList[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            trackList = it ?: ArrayList()
        }

        viewModel.getTopTracks().observe(this, observer)
    }

    inner class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TrackViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.top_track_list_item, p0, false)
            return TrackViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: TrackViewHolder, p1: Int) {
            val track = trackList[p1]
            val trackImages = track.getImages()
            val index = trackImages.size - 1
            if (trackImages.size == 0 || trackImages[index].isEmpty()) {
                // Do nothing
                // Images Array is either empty, or
                // the urls lead nowhere
            } else {
                Picasso.with(this@TopTracksFragment.context)
                    .load(trackImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(500, 500)
                    .into(p0.trackImg)
            }
            p0.trackTitle.text = track.getTitle()

            // on track selected start TrackInfoActivity
            p0.itemView.setOnClickListener {
                // Start the SecondActivity
                val intent = Intent(this@TopTracksFragment.parentContext, TrackInfoActivity::class.java)
                intent.putExtra("TRACK", track)
                (activity as MainActivity).startActivityForResult(intent, 0)

            }
        }


        override fun getItemCount(): Int {
            return trackList.size
        }

        inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var trackImg: ImageView = itemView.cover_img
            var trackTitle: TextView = itemView.track_title
        }
    }

}
