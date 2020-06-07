package com.example.android.kotlinmessanger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    var selectedphotoUri: Uri? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth= FirebaseAuth.getInstance()

        image_button_register.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }


        register_button_registration.setOnClickListener {
            performRegister()
        }

        already_have_an_account_text_view.setOnClickListener {
        // launch the login activity
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!= null){
            selectedphotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedphotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            image_button_register.alpha = 0f


        }
    }

    private fun performRegister(){
        val email= email_editText_registration.text.toString()
        val password= password_editText_registration.text.toString()

        if(email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this, "Please enter Email or Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity","Email is"+ email)
        Log.d("MainActivity","Password is $password")

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity","Successfully created user with uid: ${it.result?.user?.uid}")
                uploadImafeToFirebaseStorage()
            }

            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
     private fun uploadImafeToFirebaseStorage(){
         if(selectedphotoUri == null) return

         val filename= UUID.randomUUID().toString()
         val ref= FirebaseStorage.getInstance().getReference("/images/$filename")

         ref.putFile(selectedphotoUri!!)
             .addOnSuccessListener {
                 Log.d("RegisterActivity","Successfully uploades image: ${it.metadata?.path}")

                 ref.downloadUrl.addOnSuccessListener {
                     Log.d("RegisterActivity","File Location: $it")

                     saveUserToFirebaseDatabase(it.toString())
                 }
             }
     }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid= auth.uid ?: ""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user=User(uid,username_editText_registration.text.toString(),profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","Failed to save user to database: ${it.message}")
            }
        val intent=Intent(this,LatestMessagesActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
