package com.bnsantos.github.gist.comment.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bnsantos.github.gist.comment.App
import com.bnsantos.github.gist.comment.R
import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.ui.activity.OpenLinkInterface
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentAdapter(private val listener: OpenLinkInterface) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {
    var comments = mutableListOf<Comment>()

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        return CommentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false), listener)
    }

    inner class CommentHolder(view: View, private val listener: OpenLinkInterface) : RecyclerView.ViewHolder(view) {
        fun bind(comment: Comment) {
            itemView.login.text = comment.user.login
            App.loadImage(itemView.avatar, comment.user.avatar, 100, 100)
            itemView.body.text = comment.body
            itemView.date.text = App.formatDate(comment.createdAt)

            itemView.login.setOnClickListener {
                listener.openLink(comment.user.url)
            }

            itemView.avatar.setOnClickListener {
                listener.openLink(comment.user.url)
            }
        }
    }
}