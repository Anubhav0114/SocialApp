package com.example.socialapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.socialapp.daos.postDao
import com.example.socialapp.databinding.ActivityCreatePostBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*

private const val TAG = "createPostActivity"
private const val PICKED_IMAGE_CODE = 123
lateinit var photoUri : Uri
private lateinit var url : String
private lateinit var binding : ActivityCreatePostBinding
private lateinit var  storageReference  : StorageReference
private lateinit var progressBar: ProgressBar
class createPostActivity : AppCompatActivity() {
    private  var url  = ""
    private  var currentTime : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)


        val postDao = postDao()

        progressBar = binding.imgProgress
        storageReference = FirebaseStorage.getInstance().reference
        binding.postButton.setOnClickListener(){
            val text = post_text.text.toString().trim()
            if (text.isEmpty() || url.isEmpty()){
                Toast.makeText(this, "Description/Image can not be Empty" , Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                postDao.addPost(text , url , currentTime)
                val intent = Intent(this, MainActivity::class.java)
               startActivity(intent)
                startActivity(intent)
                finish()
            }
        }


        binding.img.setOnClickListener{

            currentTime = System.currentTimeMillis()
            var imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            binding.img.isEnabled = false
            binding.imgDelete.visibility = View.VISIBLE
            if (imagePickerIntent.resolveActivity(packageManager) != null){
                startActivityForResult(imagePickerIntent , PICKED_IMAGE_CODE )
            }
        }

        binding.imgDelete.setOnClickListener{
            //binding.img.setImageResource(R.drawable.ic_baseline_add_a_photo_24)
            url = ""
            binding.img.isEnabled = true
            binding.imgDelete.visibility = View.GONE
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKED_IMAGE_CODE && resultCode == RESULT_OK){
            photoUri = data?.data!!
            binding.img.setImageURI(photoUri)

                var photoReference = storageReference.child("images/$currentTime-photo.jpg")
                var uploadTask = photoReference.putFile(photoUri)

            progressBar.visibility = View.VISIBLE
            binding.postButton.isEnabled = false

                var urlTask = uploadTask.continueWithTask{ task ->

                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                        Log.i(TAG, " Exception while url task ", task.exception)
                    }
                    photoReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        url = task.result.toString()
                        progressBar.visibility = View.GONE
                        binding.postButton.isEnabled = true
                    } else {
                        Toast.makeText(
                            this,
                            "Error while fetching url in urlTask",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.i(TAG, "Error while fetching url in urlTask")
                    }
            }


        }
        else{
            Toast.makeText(this,"Image picking action canceled" , Toast.LENGTH_LONG).show()
            binding.img.isEnabled = true
            binding.imgDelete.visibility = View.GONE
        }
    }


}