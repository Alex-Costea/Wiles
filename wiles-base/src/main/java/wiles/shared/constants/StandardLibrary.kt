package wiles.shared.constants

object StandardLibrary {
    val STANDARD_LIBRARY_TEXT = """
        let true := __INTERNAL "TRUE"
        let false := __INTERNAL "FALSE"
        let nothing := __INTERNAL "NOTHING"
        let Int := __INTERNAL "INT"
        let Text := __INTERNAL "TEXT"
        let Decimal := __INTERNAL "DECIMAL"
        let Anything := __INTERNAL "ANYTHING"
    """.trimIndent()
}