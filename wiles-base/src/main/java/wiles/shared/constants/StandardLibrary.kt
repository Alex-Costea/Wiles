package wiles.shared.constants

object StandardLibrary {
    val STANDARD_LIBRARY_TEXT = """
        let true := __INTERNAL "true"
        let false := __INTERNAL "false"
        let nothing := __INTERNAL "nothing"
        let int := __INTERNAL "int"
        let text := __INTERNAL "text"
        let decimal := __INTERNAL "decimal"
    """.trimIndent()
}