package cz.zcu.maturao.tetris.logic

import java.io.Serializable
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Hrací pole
 */
class Stack : Serializable {
    companion object {
        const val WIDTH = 10
        const val HEIGHT = 20

        /**
         * parametr exponenciální funkce pro výpočet dobu pádu dle levelu
         */
        const val FALL_INTERVAL_EXP_BASE = 0.85
    }

    val shapeQueue = ShapeQueue()

    /**
     * Čtverce v hracím poli
     */
    val squares = Matrix<Square>(HEIGHT, WIDTH, Square.Empty)

    /**
     * Zda je konec hry
     */
    var gameOver = false
        private set

    /**
     * Padající blok
     */
    var block = newRandomBlock()
        private set

    /**
     * Nacachovaný náhled toho, kam by blok spadnul
     */
    @Transient
    private var _ghostBlock: Block? = null

    /**
     * Náhled, kam by padající blok spadnul
     */
    val ghostBlock: Block get() = _ghostBlock ?: createGhostBlock().also { _ghostBlock = it }

    /**
     * Čás, kdy má blok spadnout o jeden řádek
     */
    @Transient
    private var nextFallTime: Long? = null

    val score = Score()

    /**
     * Vytvoří nový náhodný blok
     */
    private fun newRandomBlock() = shapeQueue.getShape().let { shape ->
        Block(shape, -2, (squares.width / 2.0 - shape.squares.width / 2.0).roundToInt())
    }

    /**
     * Vrát čtverec z hracího pole a i mimo pole (pro kontrolu kolize)
     */
    private fun getSquare(row: Int, col: Int): Square = when {
        col !in 0 until squares.width -> Square.Full.Black
        row >= squares.height -> Square.Full.Black
        row < 0 -> Square.Empty
        else -> squares[row, col]
    }

    /**
     * Zda blok koliduje s hracím polem
     */
    private fun collidesWith(block: Block) =
        block.shape.squares.withIndices().any { (i, j, shapeSquare) ->
            shapeSquare.collidesWith(getSquare(block.row + i, block.col + j))
        }

    /**
     * Přidá blok do hracího pole
     */
    private fun add(block: Block) {
        for ((i, j, square) in block.shape.squares.withIndices()) {
            if (square is Square.Empty) continue

            val row = block.row + i
            val col = block.col + j

            if (row in 0 until squares.height && col in 0 until squares.width) {
                squares[row, col] = square
            } else {
                gameOver = true
            }
        }
        val linesCleared = clearFullRows()
        if (linesCleared > 0) {
            score.update(linesCleared)
        }
    }

    /**
     * Posune řádek dolů
     */
    private fun moveRowDown(row: Int, numRows: Int) {
        for (col in 0 until squares.width) {
            squares[row + numRows, col] = squares[row, col]
            squares[row, col] = Square.Empty
        }
    }

    /**
     * Zda je řádek plný
     */
    private fun isRowFull(row: Int) = (0 until squares.width)
        .asSequence()
        .map { col -> squares[row, col] }
        .all { it is Square.Full }

    /**
     * Vyčistí řádek
     */
    private fun clearRow(row: Int) = (0 until squares.width)
        .forEach { col -> squares[row, col] = Square.Empty }

    /**
     * Vyčístí všechny plné řádky, posune řadky nad nimi, a vráti jejich počet
     */
    private fun clearFullRows(): Int {
        var cleared = 0
        for (row in squares.height - 1 downTo 0) {
            if (isRowFull(row)) {
                clearRow(row)
                cleared++
            } else if (cleared > 0) {
                moveRowDown(row, cleared)
            }
        }
        return cleared
    }

    /**
     * Vyresetuje čas, kdy má blok spadnout
     */
    private fun resetFallTime() {
        val fallInterval = (FALL_INTERVAL_EXP_BASE.pow(score.level) * 1000).roundToLong()
        nextFallTime = System.currentTimeMillis() + fallInterval
    }

    /**
     * Pokus o otočení bloku
     */
    fun rotateBlock() {
        val rotated = block.rotated()
        if (collidesWith(rotated)) return

        block = rotated
        resetGhostBlock()
    }

    /**
     * Možný výsledek po pokusu o nastavení pozice bloku
     */
    enum class BlockMoveResult {
        /**
         * S blokem nebyla provedena žádná akce
         */
        NONE,

        /**
         * Blok by při posunu na danou pozici byl v kolizi s herním polem
         */
        COLLISION,

        /**
         * Blok byl posunut na danou pozici
         */
        MOVED
    }

    /**
     * Pokus o nastavení sloupce bloku
     */
    fun setBlockCol(col: Int): BlockMoveResult {
        if (col == block.col) return BlockMoveResult.NONE

        val range =
            if (col > block.col) (block.col + 1)..col
            else (block.col - 1) downTo col

        var result = BlockMoveResult.MOVED
        var isMoved = false
        // posouvám po jednom sloupci, aby nemohl blok procházet skrz zeď
        for (subCol in range) {
            val moved = block.moved(block.row, subCol)

            if (collidesWith(moved)) {
                result = BlockMoveResult.COLLISION
                break
            }

            block = moved
            isMoved = true
        }

        if (isMoved) resetGhostBlock()

        return result
    }

    /**
     * Pokus o nastavení řádku bloku
     */
    fun setBlockRow(row: Int): BlockMoveResult {
        if (row <= block.row) return BlockMoveResult.NONE
        resetFallTime()

        // posouvám po jednom řádku, aby nemohl blok procházet skrz plné čtverce v hracím poli
        for (subRow in (block.row + 1)..row) {
            val moved = block.moved(subRow, block.col)

            if (collidesWith(moved)) {
                add(block)
                block = newRandomBlock()
                resetGhostBlock()
                return BlockMoveResult.COLLISION
            } else {
                block = moved
            }
        }

        return BlockMoveResult.MOVED
    }

    /**
     * Zkontroluje, zda nemá padající blok spadnout o jeden řádek
     * a pokud ano, tak ho posune o jeden řádek
     */
    fun checkFall() {
        val nextFallTime = nextFallTime

        if (nextFallTime == null) {
            resetFallTime()
        } else if (System.currentTimeMillis() > nextFallTime) {
            setBlockRow(block.row + 1)
        }
    }

    /**
     * Vynuluje ghostBlock cache
     */
    private fun resetGhostBlock() {
        _ghostBlock = null
    }

    /**
     * Vytvoří ghost block, který slouží jako náhled, kam spadne padající blok
     */
    private fun createGhostBlock(): Block {
        var ghostBlock = block

        while (true) {
            val moved = ghostBlock.moved(ghostBlock.row + 1, ghostBlock.col)
            if (collidesWith(moved)) break
            ghostBlock = moved
        }

        return ghostBlock
    }
}
