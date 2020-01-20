package com.logicaldp.track.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.logicaldp.track.DetailsActivity

import com.logicaldp.track.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class ListFragment() : Fragment() {
    lateinit var root:View
    lateinit var databaseuser: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val drivers = ArrayList<String>()
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        databaseuser = FirebaseDatabase.getInstance().getReference("users")
        val riders = ArrayList<String>()
        databaseuser.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(it: DataSnapshot) {
                it.children.forEach { it2 ->
                    if (it2.child("type").value == "driver") {
                        drivers.add(it2.key.toString())
                    }
                }
                for (driver in drivers) {
                    val dataSnapshot = it.child(driver)
                    val view = LayoutInflater.from(context).inflate(R.layout.list_item, null)
//                    Picasso.get().load(dataSnapshot.child("image").value.toString())
//                        .into(view.image)
                    view.name.text = dataSnapshot.child("name").value.toString()
                    view.uid.text = dataSnapshot.child("id").value.toString()
                    view.route_no.text =
                        dataSnapshot.child("route").getValue(Int::class.java).toString()
                    root.list.addView(view)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        root.add_button.setOnClickListener {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra("driver", true)
            startActivity(intent)
        }
        return root
    }


}
