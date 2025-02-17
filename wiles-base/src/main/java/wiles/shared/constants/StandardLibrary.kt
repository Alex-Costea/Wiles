package wiles.shared.constants

object StandardLibrary {
    final val STANDARD_LIBRARY_TEXT = """
        let true := __INTERNAL "true"
        let false := __INTERNAL "false"
        let nothing := __INTERNAL "nothing"
    """.trimIndent()
}