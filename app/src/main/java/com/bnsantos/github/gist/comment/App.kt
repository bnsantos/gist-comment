package com.bnsantos.github.gist.comment

import android.app.Application
import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    companion object {
        fun loadImage(view: SimpleDraweeView, url: String, w: Int, h: Int) {
            val uri = Uri.parse(url)
            val request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(ResizeOptions(w, h))
                    .build()

            val hierarchy = view.hierarchy
            val progressBarImage = ProgressBarDrawable()
            progressBarImage.isVertical = true

            hierarchy.setProgressBarImage(progressBarImage)
            hierarchy.roundingParams = RoundingParams.asCircle()
            view.hierarchy = hierarchy

            val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(view.controller)
                    .build()
            view.controller = controller
        }

        private val dateFormat: SimpleDateFormat by lazy {
            SimpleDateFormat("HH:mm dd/MM/yy", Locale.getDefault())
        }

        fun formatDate(date: Date) = dateFormat.format(date)!!
    }

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}