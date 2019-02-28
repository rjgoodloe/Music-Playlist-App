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
import kotlinx.android.synthetic.main.fragment_tracks_by_artist_list.*
import kotlinx.android.synthetic.main.result_list_item.view.*

@SuppressLint("ValidFragment")
class TracksByArtistListFragment(context: Context, query: String) : Fragment() {

    private var parentContext: Context = context
    private var queryString: String = query
    private lateinit var viewModel: TrackViewModel
    private var adapter = ResultListAdapter()
    private var trackList: ArrayList<Track> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tracks_by_artist_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        val displayText = "Top Tracks by: $queryString"
        query_text.text = displayText

        // set up recyclerView
        result_items_list.layoutManager = LinearLayoutManager(parentContext)
        result_items_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        // observe updates to recyclerView as search results are received from view model
        val observer = Observer<ArrayList<Track>> {
            result_items_list.adapter = adapter
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

        viewModel.getTracksByQueryText(queryString).observe(this, observer)

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

                Picasso.with(this@TracksByArtistListFragment.context)
                    .load(trackImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(500, 500)
                    .into(p0.trackImg)
            }
            p0.trackTitle.text = track.getTitle()

            // on track selected start TrackInfoActivity
            p0.itemView.setOnClickListener {
                val intent = Intent(this@TracksByArtistListFragment.parentContext, TrackInfoActivity::class.java)
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