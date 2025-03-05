package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.errors.ValueUnusedException
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.InvalidType
import wiles.processor.utils.TypeUtils
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.WilesException

class ProcessorCodeBlock (
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {

    override fun process() {
        try {
            if(context.isCompiling){
                for (component in syntax.getComponents())
                {
                    if(component.syntaxType == SyntaxType.DECLARATION && component.details.contains(LEVEL_SCOPE_ID)) {
                        val processor = ProcessorDeclaration(component, context)
                        processor.process()
                    }
                }
            }
            for (component in syntax.getComponents()) {
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
                if(context.isCompiling && processor is ProcessorExpression)
                {
                    val type = processor.value.getType()
                    if(type is InvalidType)
                        continue
                    if(!TypeUtils.isSuperType(NOTHING_TYPE, type))
                        throw ValueUnusedException(component.getFirstLocation())
                }
            }
        }
        catch (ex : WilesException)
        {
            context.exceptions.add(ex)
        }
    }
}