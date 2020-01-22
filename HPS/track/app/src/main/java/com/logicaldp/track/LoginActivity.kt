package com.logicaldp.track

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.signin.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mAuth = FirebaseAuth.getInstance()
        ButtonSignin.setOnClickListener {
            if (EditTextEmail.text.toString() != "")
                if (EditTextPassword.text.toString() != "") {
                    mAuth.signInWithEmailAndPassword(EditTextEmail.text.toString(), EditTextPassword.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful) {
                            val user = mAuth.currentUser
                            val database = FirebaseDatabase.getInstance().getReference("users")
                            database.child(mAuth.uid.toString()).child("type").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val value =
                                        dataSnapshot.getValue(String::class.java)!!
                                    when(value){
                                        "gaurdian" ->{
                                            val intent = Intent(this@LoginActivity, MapActivity::class.java)
                                            intent.putExtra("gaurdian", true)
                                            startActivity(intent)
                                        }
                                        "driver" ->{
                                            val intent = Intent(this@LoginActivity, MapActivity::class.java)
                                            intent.putExtra("driver", true)
                                            startActivity(intent)
                                        }
                                        else ->{
                                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                            intent.putExtra("admin", true)
                                            startActivity(intent)
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
//                                database.child(user!!.uid).child("type")
//
//                            startActivity(Intent(this, MainActivity::class.java))

                            val intent = Intent(this@LoginActivity, MapActivity::class.java)
                            intent.putExtra("driver", true)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, "Authentication Failed",Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter Password",Toast.LENGTH_LONG).show()
                }
            else{
                Toast.makeText(this, "Please enter Email",Toast.LENGTH_LONG).show()
            }
        }
    }
}
