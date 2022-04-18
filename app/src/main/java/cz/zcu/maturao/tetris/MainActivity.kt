package cz.zcu.maturao.tetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cz.zcu.maturao.tetris.logic.Matrix

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val matrix = Matrix(2, 3, 0)


        Log.d("TEST", ".\n" + matrix.toString())
        Log.d("TEST", ".\n" + matrix.rotated().toString())

        super.onCreate(savedInstanceState)
        setContentView(GameView(this))
    }
}