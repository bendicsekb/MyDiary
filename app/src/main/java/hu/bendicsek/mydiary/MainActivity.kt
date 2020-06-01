package hu.bendicsek.mydiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendAnim = AnimationUtils.loadAnimation(
                this@MainActivity, R.anim.size_up
        )

        dotAnimate.startAnimation(sendAnim)


        sendAnim.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                diary.setBackgroundColor(getColor(R.color.colorAccent))
                val intent = Intent(this@MainActivity, ScrollingActivity::class.java)
                // start your next activity
                startActivity(intent)
            }
            override fun onAnimationStart(animation: Animation?) {
            }
        })



    }
}
