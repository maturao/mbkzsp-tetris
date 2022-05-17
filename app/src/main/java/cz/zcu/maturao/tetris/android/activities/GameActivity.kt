package cz.zcu.maturao.tetris.android.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.zcu.maturao.tetris.android.GameView
import cz.zcu.maturao.tetris.logic.Stack

/**
 * Aktivita, která obsahuje GameView
 */
class GameActivity : AppCompatActivity() {
    /**
     * View, na které se vykresluje hra
     */
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)

        // obnoví stav hry, pokud byl předán z MainActivity
        restoreState(intent.getBundleExtra("state"))
    }

    override fun onBackPressed() {
        if (gameView.game.stopped || gameView.game.stackController.stack.gameOver) {
            // pokud je hra zastavená, nebo je konec, tak se vrátím do menu
            exit()
        } else {
            // jinak hru zastavím
            gameView.game.stopped = true
        }
    }

    override fun onStop() {
        super.onStop()
        gameView.game.stopped = true
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        saveState(bundle)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        restoreState(bundle)
    }

    /**
     * Vrátí se k předchozí aktivitě
     */
    fun exit() {
        if (!gameView.game.stackController.stack.gameOver) {
            // pokud není konec hry, tak uložím její stav a vrátím ho předchozí aktivitě
            val data = Intent()
            val state = Bundle()
            saveState(state)
            data.putExtra("state", state)
            setResult(Activity.RESULT_OK, data)
        }
        finish()
    }

    /**
     * Uloží stav hry do Bundle
     */
    private fun saveState(bundle: Bundle) {
        bundle.putSerializable("stack", gameView.game.stackController.stack)
    }

    /**
     * Načte stav hry z Bundle
     */
    private fun restoreState(bundle: Bundle?) {
        if (bundle == null) return

        val stack = bundle.getSerializable("stack") as? Stack

        if (stack != null) {
            gameView.game.stackController.stack = stack
        }
        gameView.game.stopped = true
    }
}