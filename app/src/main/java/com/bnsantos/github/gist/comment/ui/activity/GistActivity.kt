package com.bnsantos.github.gist.comment.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.bnsantos.github.gist.comment.R
import kotlinx.android.synthetic.main.activity_gist.*

class GistActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, url: String): Intent {
            val intent = Intent(context, GistActivity::class.java)
            intent.data = Uri.parse(url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gist)

        toolbar.title = getString(R.string.gist)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        Toast.makeText(this, intent.data.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
