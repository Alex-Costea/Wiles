package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterContext
import wiles.shared.AbstractSyntaxTree

class ProcessorDeclaration(
    val syntax : AbstractSyntaxTree,
    val context : InterpreterContext
) : AbstractProcessor {
    override fun process() {
        println(syntax)
        TODO("InterpretFromDeclaration.process")
    }
}