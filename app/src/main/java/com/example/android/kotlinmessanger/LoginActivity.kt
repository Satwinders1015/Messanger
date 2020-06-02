package com.example.android.kotlinmessanger

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {

            performLogin()

        }

        back_to_register_login.setOnClickListener {
            finish()
        }

    }

    private fun performLogin(){
        val email= email_edittext_login.text.toString()
        val password= password_edittext_login.text.toString()

        if(email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this, "Please enter Email or Password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("Main","Successfully logged in user with uid: ${it.result?.user?.uid}")
            }

            .addOnFailureListener {
                Log.d("Main", "Failed to log user: ${it.message}")
            }
    }
}