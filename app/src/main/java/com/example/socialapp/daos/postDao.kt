package com.example.socialapp.daos

import android.os.Environment
import android.text.Editable
import android.util.Log
import com.example.socialapp.models.Users
import com.example.socialapp.models.expense
import com.example.socialapp.models.post
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


private const val TAG = "postDao"
class postDao {

    private val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("Posts")
    val expenseCollection = db.collection("expense")
    private var storageReference : StorageReference = FirebaseStorage.getInstance().reference
    private val auth = Firebase.auth

    fun addPost (text : String , imgUrl : String , currentTime : Long){
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(Users :: class.java)!!
            //val currentTime  = System.currentTimeMillis()
            val post =  post(text , user, currentTime , imgUrl )
            postCollection.document().set(post)
        }
    }

    fun addExpense(mainExp: String, location: String, payMode: String, currentTime: Long, text: String){
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(Users ::class.java)!!
            val expense = expense(user , mainExp  , location , payMode , currentTime , text )
            expenseCollection.document().set(expense)
        }
    }



    private fun getPostById(postId : String): Task<DocumentSnapshot>{
        return postCollection.document(postId).get()

    }

    fun updateLikes(postId : String){
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(post ::class.java)!!
            val isLiked = post.likedBy.contains(currentUserId)

            if (isLiked){
                post.likedBy.remove(currentUserId)
            }else{
                post.likedBy.add(currentUserId)
            }
            postCollection.document(postId).set(post)
        }
    }

    suspend fun deletePost(postId: String){
        deleteImage(postId)
        postCollection.document(postId)
            .delete()
            .addOnSuccessListener {
                Log.i("MainActivity" , "Post Deleted Successfully")
            }
            .addOnFailureListener{ e ->
                Log.i("MainActivity" , "Error while deleting the post" , e)
            }
    }

    private suspend fun deleteImage(postId: String){
        //var photoReference = storageReference.getReferance
        val post = getPostById(postId).await().toObject(post ::class.java)!!
        var time = post.time.toString()
        var photoRef = storageReference.child("images/$time-photo.jpg")
        photoRef.delete()
            .addOnSuccessListener {
                Log.i(TAG , "Post deleted Successfully")
            }
            .addOnFailureListener{
                Log.i(TAG , " Error while deleting the image")
            }
    }

     suspend fun getImageUrl(postId: String) {
//        val post = getPostById(postId).await().toObject(post::class.java)!!
//         var bytt : String = ""
//         var imgRef = storageReference.child("images/${post.time.toString()}-photo.jpg")
//         val ONE_MB  : Long = 1024 * 1024
//         imgRef.getBytes(ONE_MB).addOnCompleteListener {
//
//             val localFile = File.createTempFile("images" , "jpg")
//             imgRef.getFile(localFile).addOnSuccessListener {
//                 Log.i(TAG , "Saved to local File")
//             }.addOnFailureListener{
//                 Log.i(TAG , "Error while saving to local file")
//             }
//         }
//             .addOnFailureListener{
//                 Log.i(TAG , "Failure while downloading image")
//             }
//        return bytt

         val post = getPostById(postId).await().toObject(post::class.java)!!
         var imgRef = storageReference.child("images/${post.time.toString()}-photo.jpg")
         Log.i(TAG , " img ref $imgRef")

         val rootPath = File(Environment.getExternalStorageDirectory(), "SocialApp")
         if (!rootPath.exists()) {
             rootPath.mkdirs()
         }
         val localFile = File(rootPath, "imageName.txt")
         imgRef.getFile(localFile)
             .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> { Log.i(TAG, "firebase local tem file created  created $localFile") })
             .addOnFailureListener(
                 OnFailureListener { exception ->
                     Log.i(
                         TAG,
                         "local tem file not created  created $exception"
                     )
                 })



    }
}