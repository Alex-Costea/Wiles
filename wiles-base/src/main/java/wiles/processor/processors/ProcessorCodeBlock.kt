package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.WilesException
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID

class ProcessorCodeBlock (
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    override fun process() {
        try {
            val newComponents = mutableListOf<AbstractSyntaxTree>()
            val topLevelComponents = mutableListOf<AbstractSyntaxTree>()
            for (component in syntax.components)
            {
                if(component.syntaxType == SyntaxType.DECLARATION && component.details.contains(LEVEL_SCOPE_ID)) {
                    ProcessorDeclaration(component, context).process()
                    topLevelComponents.add(component)
                }
                else newComponents.add(component)
            }
            for (component in newComponents) {
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