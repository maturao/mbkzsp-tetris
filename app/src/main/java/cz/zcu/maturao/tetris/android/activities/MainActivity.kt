package cz.zcu.maturao.tetris.android.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import cz.zcu.maturao.tetris.R

/**
 * Aktivita, která obsahuje hlavní menu
 */
class MainActivity : AppCompatActivity() {
    /**
     * Uložený stav hry pro možný návrat pomocí tlačítka resume
     */
    private var gameState: Bundle? = null

    /**
     * Textview s nejvyšším skóre
     */
    private lateinit var highScoreTextView: TextView

    /**
     * Tlačítko pro spuštění nové hry
     */
    private lateinit var newGameButton: Button

    /**
     * Tlačítko pro návrat do hry
     */
    private lateinit var resumeButton: Button

    /**
     * Launcher GameActivity, který očekává návratovou hodnotu (stav hry)
     */
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

    override fun onResume() {
        super.onResume()
        updateResumeButton()
        updateHighScoreTextView()
    }

    /**
     * Vytvoří Intent pro spuštění GameActivity
     */
    private fun launchGameIntent() = Intent(this, GameActivity::class.java)

    /**
     * Aktualizuje, zda je viditelné tlačítko resume
     */
    private fun updateResumeButton() {
        resumeButton.visibility = if (gameState == null) View.INVISIBLE else View.VISIBLE
    }

    /**
     * Aktualizuje nejvyšší skóre hráče
     */
    private fun updateHighScoreTextView() {
        val sharedPreferences = getSharedPreferences("cz.zcu.maturao.tetris", MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("highScore", 0)
        val highScoreText = resources.getString(R.string.high_score)
        highScoreTextView.text = highScoreText.format(highScore).uppercase()
    }
}