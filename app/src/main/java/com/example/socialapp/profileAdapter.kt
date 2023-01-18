package com.example.socialapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.models.post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class profileAdapter(options: FirestoreRecyclerOptions<post>, private val listener : IprofileAdapter ) : FirestoreRecyclerAdapter<post, profileAdapter.ProfileViewHolder>(
options
){

    inner class ProfileViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val postText  : TextView = itemView.findViewById(R.id.main_text)
        val userText  : TextView = itemView.findViewById(R.id.user_name)
        val createdAt : TextView = itemView.findViewById(R.id.created_at)
        val likeCount : TextView = itemView.findViewById(R.id.like_count)
        val userImage : ImageView = itemView.findViewById(R.id.user_image)
        val likeButton : ImageView = itemView.findViewById(R.id.like_button)
        val deleteButton : ImageView = itemView.findViewById(R.id.btn_delete)
        val shareButton : ImageView = itemView.findViewById(R.id.btn_share)
        val  mainImage : ImageView = itemView.findViewById(R.id.main_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val viewHolder =  ProfileViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post , parent , false))
        viewHolder.likeButton.setOnClickListener{
            listener.onLikeButtonClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.deleteButton.setOnClickListener{
            listener.onDeleteButtonClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.shareButton.setOnClickListener{
            listener.onShareButtonClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int, model: post) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.time)
        val auth = Firebase.auth
        val currUserId = auth.currentUser!!.uid

        if(currUserId == model.createdBy.uid){
            holder.deleteButton.visibility = View.VISIBLE
        }
        val isLiked = model.likedBy.contains(currUserId)
        if (isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context , R.drawable.ic_liked))
        }else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context , R.drawable.ic_unliked))
        }

        if(model.imgUrl != null){
            Glide.with(holder.mainImage.context).load(model.imgUrl).into(holder.mainImage)
        }

    }


}

interface IprofileAdapter {
    fun onLikeButtonClicked(postId : String)
    fun onDeleteButtonClicked(postId: String)
    fun onShareButtonClicked(postId: String)
}