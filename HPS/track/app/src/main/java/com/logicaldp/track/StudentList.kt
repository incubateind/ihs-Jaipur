package com.logicaldp.track

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.list_item.view.*

class StudentList : AppCompatActivity() {
    lateinit var databaseuser: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)
        databaseuser = FirebaseDatabase.getInstance().getReference("user")

        val riders = intent.getStringArrayExtra("riders")
        databaseuser.addValueEventListener(object :
        ValueEventListener {
            override fun onDataChange(it: DataSnapshot) {
                for(rider in riders){
                    val dataSnapshot = it.child(rider)
                    val view = LayoutInflater.from(this@StudentList).inflate(R.layout.list_item,null)
                    Picasso.get().load(dataSnapshot.child("image").value.toString()).into(view.image)
                    view.name.text = dataSnapshot.child("name").value.toString()
                    view.uid.text = dataSnapshot.child("id").value.toString()
                    view.route_no.text = dataSnapshot.child("route").getValue(Int::class.java).toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}
