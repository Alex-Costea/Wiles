package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.WilesException

class ProcessorProgram (
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    override fun process() {
        try {
            for (component in syntax.components) {
                val processor: AbstractProcessor = when (component.syntaxType) {
                    SyntaxType.DECLARATION -> ProcessorDeclaration(component, context)
                    SyntaxType.FUNC -> TODO()
                    SyntaxType.EXPRESSION, SyntaxType.TOKEN -> ProcessorExpression(component, context)
                    SyntaxType.CODE_BLOCK -> TODO()
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
        catch (ex : WilesException)
        {
            context.exceptions.add(ex)
        }
    }
}