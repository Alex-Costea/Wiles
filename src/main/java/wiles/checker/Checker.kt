package wiles.checker

import wiles.shared.CompilationExceptionsCollection
import wiles.parser.statements.CodeBlockStatement

class Checker(program: CodeBlockStatement) {
    val exceptions = CompilationExceptionsCollection()
    init {
        exceptions.addAll(CheckIdentifiers.check(program))
    }
}