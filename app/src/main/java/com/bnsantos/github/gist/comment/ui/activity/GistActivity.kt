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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_gist.*

class GistActivity : AppCompatActivity(), OpenLinkInterface {
    companion object {
        fun getIntent(context: Context, url: String): Intent {
            val intent = Intent(context, GistActivity::class.java)
            intent.data = Uri.parse(url)
            return intent
        }
    }

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
        viewModel.load()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateUI(it) }
    }

    private fun updateUI(it: Data) {
        when (it) {
            is Data.Success -> {
                updateOwner(it.gist.owner)
                updateGist(it.gist)
                updateComments(it.comments)
            }
            is Data.Error -> {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
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

    private fun createComment() {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
    }
}
