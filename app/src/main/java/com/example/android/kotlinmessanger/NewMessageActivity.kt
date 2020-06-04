package com.example.android.kotlinmessanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title= "Select User"

        /*val adapter=GroupAdapter<GroupieViewHolder>()


        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        recycleview_newmessage.adapter= adapter*/

        fetchUsers()

    }

    companion object{
        val USER_KEY="USER_KEY"
    }

    private fun fetchUsers(){
        val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter=GroupAdapter<GroupieViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessage",it.toString())
                    val user=it.getValue(User::class.java)
                    if(user!=null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val UserItem= item as UserItem

                    val intent=Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, UserItem.user)
                    startActivity(intent)
                    finish()
                }

                recycleview_newmessage.adapter= adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_newMessage.text=user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newMessage)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}
