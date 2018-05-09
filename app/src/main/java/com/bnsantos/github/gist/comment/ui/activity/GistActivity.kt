package com.bnsantos.github.gist.comment.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bnsantos.github.gist.comment.App
import com.bnsantos.github.gist.comment.DependencyInjector
import com.bnsantos.github.gist.comment.R
import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist
import com.bnsantos.github.gist.comment.models.GistFile
import com.bnsantos.github.gist.comment.models.User
import com.bnsantos.github.gist.comment.ui.adapter.CommentAdapter
import com.bnsantos.github.gist.comment.ui.viewmodel.Data
import com.bnsantos.github.gist.comment.ui.viewmodel.GistViewModel
import com.bnsantos.github.gist.comment.ui.widget.CommentBottomSheet
import com.bnsantos.github.gist.comment.ui.widget.EditCommentDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_gist.*

class GistActivity : AppCompatActivity(), CommentListener {
    companion object {
        fun getIntent(context: Context, url: String): Intent {
            val intent = Intent(context, GistActivity::class.java)
            intent.data = Uri.parse(url)
            return intent
        }
    }

    var disposable: Disposable? = null

    private val viewModel: GistViewModel by lazy { DependencyInjector.gistViewModel }

    private val adapter: CommentAdapter by lazy {
        CommentAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gist)

        toolbar.title = getString(R.string.gist)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        viewModel.gistId = intent.data.lastPathSegment

        val layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        rv.adapter = adapter

        send.setOnClickListener {
            createComment()
        }

        showLoading(true)
        disposable = viewModel.load()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateUI(it) }
    }

    private fun updateUI(it: Data) {
        when (it) {
            is Data.SuccessLoading -> {
                updateOwner(it.gist.owner)
                updateGist(it.gist)
                updateComments(it.comments)
            }
            is Data.SuccessComment -> {
                adapter.comments.add(it.comment)
                adapter.notifyItemInserted(adapter.comments.size)
                rv.smoothScrollToPosition(adapter.comments.size)
                input.setText("")
                showCommentLoading(false)
            }
            is Data.ErrorLoading -> {
                Toast.makeText(this, R.string.error_loading_gist, Toast.LENGTH_SHORT).show()
                finish()
            }
            is Data.ErrorComment -> {
                Toast.makeText(this, R.string.error_create_comment, Toast.LENGTH_SHORT).show()
                showCommentLoading(false)
            }
        }
        showLoading(false)
    }

    private fun updateOwner(user: User) {
        App.loadImage(avatar, user.avatar, 100, 100)
        login.text = user.login

        avatar.setOnClickListener {
            openLink(user.url)
        }

        login.setOnClickListener {
            openLink(user.url)
        }
    }

    private fun updateGist(gist: Gist) {
        description.text = gist.description
        files.text = filesString(gist.files)

        description.setOnClickListener {
            openLink(gist.url)
        }

        files.setOnClickListener {
            openLink(gist.url)
        }
    }

    private fun filesString(files: Map<String, GistFile>): String {
        return files.keys.reduce { acc, s -> "$acc, $s" }
    }

    private fun updateComments(comments: List<Comment>) {
        adapter.comments.clear()
        adapter.comments.addAll(comments)
        adapter.notifyDataSetChanged()
        rv.smoothScrollToPosition(comments.size)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.d(MainActivity::class.java.simpleName, "No Intent available to handle action")
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            progress.visibility = View.VISIBLE
            avatar.visibility = View.GONE
            login.visibility = View.GONE
            description.visibility = View.GONE
            files.visibility = View.GONE
            input.visibility = View.GONE
            send.visibility = View.GONE
            progressComment.visibility = View.GONE
        } else {
            progress.visibility = View.GONE
            avatar.visibility = View.VISIBLE
            login.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            files.visibility = View.VISIBLE
            input.visibility = View.VISIBLE
            send.visibility = View.VISIBLE
        }
    }

    private fun showCommentLoading(show: Boolean) {
        if (show) {
            progressComment.visibility = View.VISIBLE
            send.visibility = View.GONE
            input.isEnabled = false
        } else {
            progressComment.visibility = View.GONE
            send.visibility = View.VISIBLE
            input.isEnabled = true
        }
    }

    private fun createComment() {
        if (input.text.isNotEmpty()) {
            showCommentLoading(true)
            disposable = viewModel.createComment(input.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateUI(it)
                    }
        }
    }

    override fun onStop() {
        super.onStop()
        disposable?.let {
            it.dispose()
        }
    }

    override fun onLongClick(pos: Int, comment: Comment): Boolean {
        val bottomSheet = CommentBottomSheet()
        bottomSheet.listener = object : CommentBottomSheet.Listener {
            override fun edit() {
                showEditCommentDialog(pos, comment)
            }

            override fun delete() {
                deleteComment(pos, comment)
            }
        }
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        return true
    }

    private fun deleteComment(pos: Int, comment: Comment) {
        adapter.notifyItemRemoved(pos)
        adapter.comments.removeAt(pos)
        disposable = viewModel.deleteComment(comment.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is Data.ErrorDeleteComment -> {
                            Toast.makeText(this, R.string.error_delete_comment, Toast.LENGTH_SHORT).show()
                            adapter.comments.add(pos, comment)
                            adapter.notifyItemInserted(pos)
                        }
                    }
                    updateUI(it)
                }
    }

    private fun showEditCommentDialog(pos: Int, comment: Comment) {
        val dialog = EditCommentDialog()
        dialog.previous = comment.body
        dialog.listener = object : EditCommentDialog.Listener {
            override fun edit(newBody: String) {
                editComment(pos, comment, newBody)
            }
        }
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun editComment(pos: Int, p: Comment, newBody: String) {
        adapter.replace(pos, Comment(p.id, p.user, p.createdAt, p.updatedAt, newBody))
        disposable = viewModel.editComment(p.id, newBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is Data.SuccessComment -> {
                            adapter.replace(pos, it.comment)
                        }
                        is Data.ErrorComment -> {
                            adapter.replace(pos, p)
                            Toast.makeText(this@GistActivity, R.string.error_edit_comment, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }
}
