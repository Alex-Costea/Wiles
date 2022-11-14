package wiles.checker

import wiles.parser.statements.CodeBlockStatement
import wiles.shared.CompilationExceptionsCollection

class Checker(program: CodeBlockStatement) {
    val exceptions = CompilationExceptionsCollection()
    companion object
    {
        private val defaultIdentifiers = HashMap<Int, String>()
        init {
            defaultIdentifiers[0] = "!writeline"
            defaultIdentifiers[1] = "!write"
        }
    }
    init {
        exceptions.addAll(CheckIdentifiers(defaultIdentifiers).check(program))
    }
}