package cz.zcu.maturao.tetris

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import cz.zcu.maturao.tetris.utils.debugLog
import java.io.*
import java.util.prefs.Preferences

class MainActivity : AppCompatActivity() {
    private var gameState: Bundle? = null

    private lateinit var highScoreTextView: TextView
    private lateinit var newGameButton: Button
    private lateinit var resumeButton: Button

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            gameState = result.data?.getBundleExtra("state")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        highScoreTextView = findViewById(R.id.highscoreTextView)
        newGameButton = findViewById(R.id.newGameButton)
        resumeButton = findViewById(R.id.resumeButton)

        newGameButton.setOnClickListener {
            startForResult.launch(launchGameIntent())
        }

        updateResumeButton()
        resumeButton.setOnClickListener {
            startForResult.launch(launchGameIntent().also { it.putExtra("state", gameState) })
        }
    }

    private fun launchGameIntent() = Intent(this, GameActivity::class.java)

    private fun updateResumeButton() {
        resumeButton.visibility = if (gameState == null) View.INVISIBLE else View.VISIBLE
    }

    private fun updateHighScoreTextView() {
        val sharedPreferences = getSharedPreferences("cz.zcu.maturao.tetris", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("highScore", 0)
        val highScoreText = resources.getString(R.string.high_score)
        highScoreTextView.text = highScoreText.format(highScore).uppercase()
    }

    override fun onResume() {
        super.onResume()
        updateResumeButton()
        updateHighScoreTextView()
    }
}