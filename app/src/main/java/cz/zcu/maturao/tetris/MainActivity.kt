package cz.zcu.maturao.tetris

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import cz.zcu.maturao.tetris.utils.debugLog
import java.io.*

class MainActivity : AppCompatActivity() {
    private var gameState: Bundle? = null

    private lateinit var resumeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.newGameButton).setOnClickListener(::newGame)

        resumeButton = findViewById(R.id.resumeButton)
        resumeButton.setOnClickListener(::resumeGame)
        resumeButton.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        resumeButton.visibility = if (gameState == null) View.INVISIBLE else View.VISIBLE
    }

    private fun newGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startForResult.launch(intent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            gameState = result.data?.getBundleExtra("state")
        }

    private fun resumeGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        if (gameState != null) {
            intent.putExtra("state", gameState)
        }
        startForResult.launch(intent)
    }
}