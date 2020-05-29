package com.example.android.kotlinmessanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button_registration.setOnClickListener {
            val email= email_editText_registration.text.toString()
            val password= password_editText_registration.text.toString()

            Log.d("MainActivity","Email is"+ email)
            Log.d("MainActivity","Password is $password")
        }

        already_have_an_account_text_view.setOnClickListener {
        // launch the login activity
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
