package com.example.socialapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.daos.postDao
import com.example.socialapp.models.expense
import com.example.socialapp.models.post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


private const val  TAG = "MainActivity"
class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var adapter : PostAdapter
    private lateinit var postDao : postDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.appToolBar))

        addButton.setOnClickListener{
           val intent = Intent(this , createPostActivity::class.java)
            startActivityForResult(intent , 123)
        }
        profileBtn.setOnClickListener{
            val intent = Intent(this , ProfileActivity::class.java)
            startActivity(intent)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView (){

        postDao = postDao()

        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("time", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<post>().setQuery(query , post ::class.java).build()
        adapter = PostAdapter(recyclerViewOptions , this )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        recyclerView.scrollToPosition(0)

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeButtonClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onDeleteButtonClicked(postId: String) {
        val dialogue = AlertDialog.Builder(this)
            .setTitle("You wants to delete this post.")
            .setPositiveButton("Ok" , null)
            .setNegativeButton("Cancel" , null).show()
        dialogue.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
            GlobalScope.launch {
                postDao.deletePost(postId)
            }
            dialogue.dismiss()
        }

    }

    override fun onShareButtonClicked(postId: String) {

    }

}