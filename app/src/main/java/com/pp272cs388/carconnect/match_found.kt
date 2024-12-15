package com.pp272cs388.carconnect

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class match_found : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_found)

        val matchTextView: TextView = findViewById(R.id.matchTextView)
        animateTextView(matchTextView)
    }

    private fun animateTextView(textView: TextView) {
        textView.alpha = 0f
        textView.scaleX = 0.5f
        textView.scaleY = 0.5f

        val fadeInAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        val scaleXAnimator = ObjectAnimator.ofFloat(textView, "scaleX", 0.5f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(textView, "scaleY", 0.5f, 1f)

        fadeInAnimator.duration = 1500
        scaleXAnimator.duration = 1500
        scaleYAnimator.duration = 1500

        fadeInAnimator.start()
        scaleXAnimator.start()
        scaleYAnimator.start()

        scaleYAnimator.interpolator = BounceInterpolator()
    }
}
