package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.shared.AbstractSyntaxTree

class ProcessorLevelScopeChecker(syntax: AbstractSyntaxTree, context: InterpreterContext)
    : ProcessorDeclaration(syntax, context) {
    override val isCheckingLevelScope = true
}