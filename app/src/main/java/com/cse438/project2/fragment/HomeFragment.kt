package com.cse438.project2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.cse438.project2.R
import com.cse438.project2.activity.MainActivity
import com.cse438.project2.enums.UserInterfaceState

@SuppressLint("ValidFragment")
class HomeFragment(context: Context) : Fragment() {

    private var parentContext: Context = context



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View? = inflater.inflate(R.layout.fragment_home, container, false)

        // show tracks on top of the charts as home/starting page
        (activity as MainActivity).fm.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frag_placeholder, TopTracksFragment(this.parentContext))
            .commit()

        // init search functionality
        val searchEt = view!!.findViewById<EditText>(R.id.search_edit_text)
        searchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = searchEt.text
                searchEt.setText("")
                if (searchText.toString() == "") {
                    val toast = Toast.makeText(this.context, "Please enter text", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return@setOnEditorActionListener true
                } else {
                    performSearch(searchText.toString())
                    return@setOnEditorActionListener false
                }
            }

            return@setOnEditorActionListener false
        }
        return view
    }

    private fun performSearch(query: String) {

        // show search by artist fragment
        (activity as MainActivity).fm.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frag_placeholder, ArtistListFragment(this.parentContext, query))
            .commit()

    }
}