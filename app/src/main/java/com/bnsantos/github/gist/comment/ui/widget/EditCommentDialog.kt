package com.bnsantos.github.gist.comment.ui.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.bnsantos.github.gist.comment.R
import kotlinx.android.synthetic.main.dialog_edit_comment.view.*

class EditCommentDialog : DialogFragment() {
    private val dialogListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                listener.edit(input.text.toString())
            }
            else -> {
                dismiss()
            }
        }
    }

    private lateinit var input: EditText
    lateinit var listener: Listener
    var previous: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
                .setPositiveButton(R.string.ok, dialogListener)
                .setNegativeButton(R.string.cancel, dialogListener)
        val view = View.inflate(context, R.layout.dialog_edit_comment, null)
        input = view.input

        if (previous != null) {
            input.setText(previous)
        }
        builder.setView(view)
        return builder.create()
    }

    override fun dismiss() {
        hideKeyboard()
        super.dismiss()
    }

    private fun hideKeyboard() {
        val im = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(input.windowToken, 0)
    }

    interface Listener {
        fun edit(newBody: String)
    }
}