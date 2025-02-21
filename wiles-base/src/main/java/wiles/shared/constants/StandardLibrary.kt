package wiles.shared.constants

object StandardLibrary {
    val STANDARD_LIBRARY_TEXT = """
        let true := __INTERNAL "true"
        let false := __INTERNAL "false"
        let nothing := __INTERNAL "nothing"
        let Int := __INTERNAL "int"
        let Text := __INTERNAL "text"
        let Decimal := __INTERNAL "decimal"
    """.trimIndent()
}