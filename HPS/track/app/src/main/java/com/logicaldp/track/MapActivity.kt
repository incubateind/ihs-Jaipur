package com.logicaldp.track

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapScene
import com.here.sdk.mapviewlite.MapStyle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.confirm_going.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import com.here.sdk.mapviewlite.MapViewLite

class MapActivity : AppCompatActivity() {
    lateinit var databaseuser:DatabaseReference
    lateinit var databaseroute: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val user = FirebaseAuth.getInstance().currentUser?.uid

        if (user != null) {
            databaseroute = FirebaseDatabase.getInstance().getReference("routes")
            databaseuser = FirebaseDatabase.getInstance().getReference("users")
            val gaurdian = intent.getBooleanExtra("gaurdian", false)
            val driver = intent.getBooleanExtra("driver", false)
            var route = intent.getIntExtra("route", -1)
            if (gaurdian) {
                val confirmGoing = layoutInflater.inflate(R.layout.confirm_going, null)

                confirmGoing.button_absent.setOnClickListener {
                    message_container.removeAllViews()
                    TODO("processing")
                }
                confirmGoing.button_absent.setOnClickListener {
                    message_container.removeAllViews()
                    TODO("processing")
                }
                message_container.addView(confirmGoing)
            }
            if(driver) {
                databaseuser.child(user).child("route").addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val value =
                            dataSnapshot.getValue(Int::class.java)!!
                        route = value
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            val riders = ArrayList<String>()
            var driverUid :String
            if (route != -1) {
                databaseroute.child(route.toString()).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        driverUid = dataSnapshot.child("driver").getValue(String::class.java)!!
                        dataSnapshot.child("riders").children.forEach {
                            riders.add(it.getValue(String::class.java)!!)
                        }
                        updateUI(driverUid,riders)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            else{
                onBackPressed()
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun loadMapScene() { // Load a scene from the SDK to render the map with a map style.
        map_view.getMapScene().loadScene(MapStyle.NORMAL_DAY, object : MapScene.LoadSceneCallback {
            override fun onLoadScene(@Nullable errorCode: MapScene.ErrorCode?) {
                if (errorCode == null) {
                    map_view.getCamera().setTarget(GeoCoordinates(52.530932, 13.384915))
                    map_view.getCamera().setZoomLevel(14.toDouble())
                } else {
                }
            }
        })
    }
    private fun updateUI(driverUid: String, riders: ArrayList<String>) {
        databaseuser.child(driverUid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Picasso.get().load(dataSnapshot.child("image").value.toString()).into(driver_details.image)
                driver_details.name.text = dataSnapshot.child("name").value.toString()
                driver_details.uid.text = dataSnapshot.child("id").value.toString()
                driver_details.route_no.text = dataSnapshot.child("route").getValue(Int::class.java).toString()
                riderList.setOnClickListener {
                    val intent = Intent(this@MapActivity, StudentList::class.java)
                    intent.putExtra("riders", riders)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}