package com.sleewell.sleewell.modules.permissions

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import com.sleewell.sleewell.R

class DndAccessTutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dnd_access_tutorial)

        val button = findViewById<Button>(R.id.buttonProceed)
        button.setOnClickListener {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }

        val animateSquirrel = findViewById<ImageView>(R.id.iconSplashScreen)
        val animated : Animatable2 = animateSquirrel.drawable as Animatable2
        animated.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                animated.start()
            }
        })
        animated.start()
    }
}