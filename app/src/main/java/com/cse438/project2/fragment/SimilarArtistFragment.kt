package com.cse438.project2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import com.cse438.project2.model.Artist
import com.cse438.project2.viewmodel.ArtistViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.artist_list_item.view.*
import kotlinx.android.synthetic.main.fragment_similar_artist.*

@SuppressLint("ValidFragment")
class SimilarArtistFragment(context: Context, query: String): Fragment() {

    private var parentContext: Context = context
    private var queryString: String = query

    private lateinit var viewModel: ArtistViewModel
    private var adapter = ArtistListAdapter()
    private var artistList: ArrayList<Artist> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_similar_artist, container, false)
    }

    override fun onStart() {
        super.onStart()
        val displayText = "Artist similar to: $queryString"
        query_text.text = displayText

        // set up recyclerView
        artist_list.layoutManager = LinearLayoutManager(parentContext)
        artist_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(ArtistViewModel::class.java)

        // observe updates to recyclerView as the come from async task
        val observer = Observer<ArrayList<Artist>> {
            artist_list.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return artistList[p0].getName() == artistList[p1].getName()
                }

                override fun getOldListSize(): Int {
                    return artistList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return artistList[p0] == artistList[p1]
                }
            })
            result.dispatchUpdatesTo(adapter)
            artistList = it ?: ArrayList()
        }

        viewModel.getSimilarArtistsByQueryText(queryString).observe(this, observer)

    }

    inner class ArtistListAdapter: RecyclerView.Adapter<ArtistListAdapter.ArtistListViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ArtistListViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.artist_list_item, p0, false)
            return ArtistListViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ArtistListViewHolder, p1: Int) {
            val artist = artistList[p1]
            val artistImages = artist.getImages()
            var index = artistImages.size - 1
            if (artistImages.size == 0 || artistImages[index].isEmpty()) {
                // Do nothing
                // Images Array is either empty, or
                // the urls lead nowhere
            } else {
                Picasso.with(this@SimilarArtistFragment.context)
                    .load(artistImages[index])
                    .transform(RoundedCornersTransformation(30, 10))
                    .resize(500,500)
                    .into(p0.artistImg)
            }
            p0.artistName.text = artist.getName()

            // on artist selected show top tracks by artist fragment
            p0.itemView.setOnClickListener {
                (activity as MainActivity).fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.frag_placeholder, TracksByArtistListFragment(this@SimilarArtistFragment.parentContext, artist.getName()))
                    .commit()
            }
        }

        override fun getItemCount(): Int {
            return artistList.size
        }



        inner class ArtistListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var artistImg: ImageView = itemView.artist_img
            var artistName: TextView = itemView.artist_name
        }
    }
}