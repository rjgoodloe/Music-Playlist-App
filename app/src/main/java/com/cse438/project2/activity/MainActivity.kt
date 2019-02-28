package com.cse438.project2.activity

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.cse438.project2.R
import com.cse438.project2.fragment.*
import com.cse438.project2.model.Track
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val fm = this.supportFragmentManager!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init 2 page tab view
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager, this, checkNetworkConnected())
        viewpager_main.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager_main)

    }

    private fun checkNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return if (networkInfo == null) {
            Log.e("NETWORK", "not connected")
            false
        } else {

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> displayDialog(R.layout.dialog_info_last_fm_api)
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayDialog(layout: Int) : Boolean {
        val dialog = Dialog(this)
        dialog.setContentView(layout)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        return true
    }


    // when info activity returns, check whether to display similar tracks, or artists, or neither
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if(data!!.extras!!.containsKey("ARTIST")) {
                    val track = data.extras!!.getSerializable("ARTIST") as Track
                    fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frag_placeholder, SimilarArtistFragment(this, track.getArtist()))
                        .commitAllowingStateLoss()
                }else{
                    val track = data.extras!!.getSerializable("TITLE") as Track
                    fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frag_placeholder, SimilarTrackFragment(this, track.getArtist(), track.getTitle()))
                        .commitAllowingStateLoss()}


//

            }
        }

    }


    class MyPagerAdapter(fm: FragmentManager, context: Context, private var isNetworkConnected: Boolean) :
        FragmentPagerAdapter(fm) {
        private val parentContext = context

        override fun getItem(position: Int): Fragment {
            if (!this.isNetworkConnected) {
                return NoConnectionFragment()
            }
            return when (position) {
                0 -> {
                    HomeFragment(this.parentContext)
                }
                else -> PlaylistFragment(this.parentContext)
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Home"
                else -> {
                    return "Playlist"
                }
            }
        }
    }
}


