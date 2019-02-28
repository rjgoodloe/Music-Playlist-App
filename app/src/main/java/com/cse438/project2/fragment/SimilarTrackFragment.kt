package com.cse438.project2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
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
import com.cse438.project2.viewmodel.TrackViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_similar_tracks.*
import kotlinx.android.synthetic.main.result_list_item.view.*

@SuppressLint("ValidFragment")
class SimilarTrackFragment(context: Context, artist: String, track: String) : Fragment() {

    private var parentContext: Context = context
    private var _artist: String = artist
    private var _track: String = track

    private lateinit var viewModel: TrackViewModel
    private var adapter = ResultListAdapter()
    private var trackList: ArrayList<Track> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_similar_tracks, container, false)
    }

    override fun onStart() {
        super.onStart()
        val displayText = "Tracks similar to: $_track"
        query_text.text = displayText

        // set up recyclerView
        track_list.layoutManager = LinearLayoutManager(parentContext)
        track_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        // observe updates to recyclerView as search results are received from view model
        val observer = Observer<ArrayList<Track>> {
            track_list.adapter = adapter
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

        viewModel.getSimilarTracksByQueryText(_artist,_track).observe(this, observer)

    }

    inner class ResultListAdapter : RecyclerView.Adapter<ResultListAdapter.ResultListViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ResultListViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.result_list_item, p0, false)
            return ResultListViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ResultListViewHolder, p1: Int) {
            val track = trackList[p1]
            val trackImages = track.getImages()
            val index = trackImages.size - 1
            if (trackImages.size == 0 || trackImages[index].isEmpty()) {
                // Do nothing
                // Images Array is either empty, or
                // the urls lead nowhere
            } else {

                Picasso.with(this@SimilarTrackFragment.context)
                    .load(trackImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(500, 500)
                    .into(p0.trackImg)
            }
            p0.trackTitle.text = track.getTitle()

            // on track selected start TrackInfoActivity
            p0.itemView.setOnClickListener {
                val intent = Intent(this@SimilarTrackFragment.parentContext, TrackInfoActivity::class.java)
                intent.putExtra("TRACK", track)
                (activity as MainActivity).startActivityForResult(intent, 0)
            }
        }

        override fun getItemCount(): Int {
            return trackList.size
        }

        inner class ResultListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var trackImg: ImageView = itemView.cover_img
            var trackTitle: TextView = itemView.track_title
        }
    }
}