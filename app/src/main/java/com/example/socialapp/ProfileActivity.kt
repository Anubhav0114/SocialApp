package com.example.socialapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialapp.daos.UserDao
import com.example.socialapp.daos.postDao
import com.example.socialapp.models.Users
import com.example.socialapp.models.post
import com.example.socialapp.models.user
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "ProfileActivity"
class ProfileActivity : AppCompatActivity()  , IprofileAdapter{

    private lateinit var adapter : profileAdapter
    private lateinit var postDao : postDao
    private lateinit var auth : FirebaseAuth
    private var signedInUser : Users? = null
    private lateinit var userDao : UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = Firebase.auth
        userDao = UserDao()
        val currentUser = auth.currentUser.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser).get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(Users::class.java)!!
                tv_name.text = signedInUser?.displayName
                Glide.with(profile_img.context).load(signedInUser?.imageUrl).circleCrop().into(profile_img)
            }.addOnFailureListener{ exception ->
                android.util.Log.i(TAG, "Failure fetching User" , exception)
            }

        onBackPressed()

        setUpRecyclerView()
        sign_out.setOnClickListener{
            logOut()
        }

    }

    private fun logOut(){
        auth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setUpRecyclerView (){

        postDao = postDao()
        val currentUser = auth.currentUser!!.uid
        val postCollection = postDao.postCollection.whereEqualTo("createdBy.uid" ,currentUser)
        val query = postCollection.orderBy("time", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<post>().setQuery(query , post ::class.java).build()
        adapter = profileAdapter(recyclerViewOptions , this )
        recyclerView2.adapter = adapter
        recyclerView2.layoutManager = LinearLayoutManager(this)

    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
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