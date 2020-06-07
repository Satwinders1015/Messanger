package com.example.android.kotlinmessanger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*


class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var chatPartenerUser: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_latest_message.text=chatMessage.text

        val chatPartenerId:String

        if(chatMessage.fromId== FirebaseAuth.getInstance().uid){
            chatPartenerId=chatMessage.toId
        }else{
            chatPartenerId=chatMessage.fromId
        }
        val ref= FirebaseDatabase.getInstance().getReference("/users/$chatPartenerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartenerUser=p0.getValue(User::class.java)
                viewHolder.itemView.username_latest_message.text=chatPartenerUser?.username

                val targetImageView=viewHolder.itemView.imageView_latestM_row
                Picasso.get().load(chatPartenerUser?.profileImageUrl).into(targetImageView)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}