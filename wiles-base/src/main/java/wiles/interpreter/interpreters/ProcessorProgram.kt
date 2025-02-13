package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterContext
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType

class ProcessorProgram (
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    override fun process() {
        for(component in syntax.components)
        {
            val processor : AbstractProcessor = when(component.syntaxType){
                SyntaxType.DECLARATION -> ProcessorDeclaration(component, context)
                SyntaxType.FUNC -> TODO()
                SyntaxType.EXPRESSION -> TODO()
                SyntaxType.CODE_BLOCK -> TODO()
                SyntaxType.TOKEN -> TODO()
                SyntaxType.TYPEDEF -> TODO()
                SyntaxType.IF -> TODO()
                SyntaxType.DICT -> TODO()
                SyntaxType.DATA -> TODO()
                SyntaxType.RETURN -> TODO()
                SyntaxType.WHILE -> TODO()
                SyntaxType.FUNC_CALL -> TODO()
                SyntaxType.LIST -> TODO()
                SyntaxType.FOR -> TODO()
            }
            processor.process()
        }
    }
}