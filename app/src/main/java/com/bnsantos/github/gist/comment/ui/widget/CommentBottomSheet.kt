package com.bnsantos.github.gist.comment.ui.widget

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bnsantos.github.gist.comment.R
import kotlinx.android.synthetic.main.bottom_sheet_comment.view.*

class CommentBottomSheet : BottomSheetDialogFragment() {
    lateinit var listener: Listener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_comment, container, false)
        view.edit.setOnClickListener {
            listener.edit()
            dismiss()
        }

        view.delete.setOnClickListener {
            listener.delete()
            dismiss()
        }
        return view
    }

    interface Listener {
        fun edit()
        fun delete()
    }
}