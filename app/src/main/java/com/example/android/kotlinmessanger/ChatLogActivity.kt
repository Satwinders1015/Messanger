package com.example.android.kotlinmessanger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG="Chat Log"
    }

    val adapter= GroupAdapter<GroupieViewHolder>()

    var toUser: User?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recycleView_ChatLog.adapter= adapter



        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser.username

       // setupDummyData()
        listenForMessage()

        sendButton_chatlog.setOnClickListener {
            Log.d(TAG,"aTTEMPT TO SEND MSG")
            performSendMesage()
        }


    }


    private fun listenForMessage(){
        val ref=FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatMessage::class.java)

                if(chatMessage!=null){
                    Log.d(TAG,"message is ${chatMessage.text}")


                    if(chatMessage.fromId==FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add((ChatFromItem(chatMessage.text, currentUser!!)))

                    }
                    else{
                        adapter.add(ChatToItem(chatMessage.text,toUser!!))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMesage(){
        val text= edittext_chatLog.text.toString()

        val refer=FirebaseDatabase.getInstance().getReference("/messages").push()
        val fromId= FirebaseAuth.getInstance().uid

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user.uid

        val chatMessage= ChatMessage(refer.key!!, text, fromId!!, toId, System.currentTimeMillis()/1000)
        refer.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat msg ${refer.key}")
            }
    }

}



class ChatFromItem(val text:String,val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.viewText_from_row.text=text

        val uri= user.profileImageUrl
        val targetImageView= viewHolder.itemView.imageView_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text:String,val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text=text

        val uri= user.profileImageUrl
        val targetImageView= viewHolder.itemView.imageView_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
       return R.layout.chat_to_row
    }
}