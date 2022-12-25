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

        addButton.setOnClickListener{
           val intent = Intent(this , createPostActivity::class.java)
            startActivityForResult(intent , 123)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView (){

        postDao = postDao()
        /*
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("time", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<post>().setQuery(query , post ::class.java).build()
        adapter = PostAdapter(recyclerViewOptions , this )

         */


        val postCollection = postDao.expenseCollection
        val query = postCollection.orderBy("time", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<expense>().setQuery(query , expense::class.java).build()

        adapter = PostAdapter(recyclerViewOptions , this )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
      //  recyclerView.scrollToPosition(6)
        Log.i(TAG , "setup Recyclerview  function")

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
//        GlobalScope.launch {
//            postDao.getImageUrl(postId)
//        }


       // Log.i(TAG , "the image bytes are $imgByte")



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK){
            recyclerView.scrollToPosition(0)
            Log.i(TAG , "On Activity Result")
        }
    }
}