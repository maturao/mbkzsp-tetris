package cz.zcu.maturao.tetris.logic

object Shapes {
    val I = Shape.fromString(
        Square.Full.Cyan, """
                ,,,,
                ####
                ,,,,
                ,,,,
            """.trimIndent()
    )

    val J = Shape.fromString(
        Square.Full.Blue, """
                #,,
                ###
                ,,,
            """.trimIndent()
    )

    val L = Shape.fromString(
        Square.Full.Orange, """
                ,,#
                ###
                ,,,
            """.trimIndent()
    )

    val O = Shape.fromString(
        Square.Full.Yellow, """
                ##
                ##
            """.trimIndent()
    )

    val S = Shape.fromString(
        Square.Full.Green, """
                ,##
                ##,
                ,,,
            """.trimIndent()
    )

    val T = Shape.fromString(
        Square.Full.Purple, """
                ,#,
                ###
                ,,,
            """.trimIndent()
    )

    val Z = Shape.fromString(
        Square.Full.Red, """
                ##,
                ,##
                ,,,
            """.trimIndent()
    )

    val stop = Shape.fromString(
        Square.Full.White, """
            ##,,##
            ##,,##
            ##,,##
            ##,,##
            ##,,##
            ##,,##
        """.trimIndent()
    )
}
