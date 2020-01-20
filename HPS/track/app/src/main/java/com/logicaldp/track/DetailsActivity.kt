package com.logicaldp.track

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.datailsadd.*
import kotlinx.android.synthetic.main.datailsadd.buttonSubmit
import kotlinx.android.synthetic.main.datailsadd.editTextEmail
import kotlinx.android.synthetic.main.datailsadd.editTextName
import kotlinx.android.synthetic.main.datailsadd.editTextPhone
import kotlinx.android.synthetic.main.datailsadd.editTextRegNo
import kotlinx.android.synthetic.main.datailsadd.editTextRoute
import kotlinx.android.synthetic.main.rideradd.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rider = intent.getBooleanExtra("rider", false)
        val driver = intent.getBooleanExtra("driver", false)
        if (rider && !driver) {
            setContentView(R.layout.rideradd)
            buttonSubmit.setOnClickListener {
                if (editTextName.text.toString() != "")
                    if (editTextRegNo.text.toString() != "")
                        if (editTextRoute.text.toString() != "")
                            if (editTextEmail.text.toString() != "")
                                if (editTextPhone.text.toString() != "") {
                                    val database =
                                        FirebaseDatabase.getInstance().getReference("user")
                                    val databaseroute =
                                        FirebaseDatabase.getInstance().getReference("route")
                                    val createuser = FirebaseAuth.getInstance()
                                        .createUserWithEmailAndPassword(
                                            editTextEmail.text.toString(),
                                            "123456"
                                        )
                                        .addOnSuccessListener {
                                            database.child(it.user!!.uid).child("name")
                                                .setValue(editTextName.text.toString())
                                            database.child(it.user!!.uid).child("id")
                                                .setValue(editTextRegNo.text.toString())
                                            database.child(it.user!!.uid).child("route")
                                                .setValue(editTextRoute.text.toString())
                                            database.child(it.user!!.uid).child("email")
                                                .setValue(editTextEmail.text.toString())
                                            database.child(it.user!!.uid).child("phone")
                                                .setValue(editTextPhone.text.toString())
                                            databaseroute.child(editTextRoute.text.toString()).push().setValue(it.user!!.uid)
                                        }
                                }
            }
        }
        if (!rider && driver) {
            setContentView(R.layout.datailsadd)
            if (editTextName.text.toString() != "")
                if (editTextRegNo.text.toString() != "")
                    if (editTextRoute.text.toString() != "")
                        if (editTextEmail.text.toString() != "")
                            if (editTextPhone.text.toString() != "")
                                if (editTextCoordinates.text.toString() != "") {
                                val database = FirebaseDatabase.getInstance().getReference("user")
                                val databaseroute =
                                    FirebaseDatabase.getInstance().getReference("route")
                                val createuser = FirebaseAuth.getInstance()
                                    .createUserWithEmailAndPassword(
                                        editTextEmail.text.toString(),
                                        "123456"
                                    )
                                    .addOnSuccessListener {
                                        database.child(it.user!!.uid).child("name")
                                            .setValue(editTextName.text.toString())
                                        database.child(it.user!!.uid).child("id")
                                            .setValue(editTextRegNo.text.toString())
                                        database.child(it.user!!.uid).child("route")
                                            .setValue(editTextRoute.text)
                                        database.child(it.user!!.uid).child("email")
                                            .setValue(editTextEmail.text.toString())
                                        database.child(it.user!!.uid).child("phone")
                                            .setValue(editTextPhone.text.toString())
                                        database.child(it.user!!.uid).child("coordinates")
                                            .setValue(editTextCoordinates.text.toString())
                                        databaseroute.child(editTextRoute.text.toString()).child("driver").setValue(it.user!!.uid)
                                    }
                            }
        }
    }
}
