package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType
import wiles.processor.values.WilesNothing
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.enums.SyntaxType

open class Processor(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
): AbstractProcessor(syntax, context){

    override fun process() : Value {
        val processor: AbstractProcessor = when (syntax.syntaxType) {
            SyntaxType.DECLARATION -> ProcessorDeclaration(syntax, context)
            SyntaxType.FUNC -> TODO()
            SyntaxType.EXPRESSION -> ProcessorExpression(syntax, context)
            SyntaxType.CODE_BLOCK -> ProcessorCodeBlock(syntax, context)
            SyntaxType.TYPEDEF -> ProcessorTypeExpression(syntax, context)
            SyntaxType.IF -> TODO()
            SyntaxType.DICT -> TODO()
            SyntaxType.DATA -> TODO()
            SyntaxType.RETURN -> TODO()
            SyntaxType.WHILE -> TODO()
            SyntaxType.FUNC_CALL -> ProcessorFuncCall(syntax, context)
            SyntaxType.LIST -> TODO()
            SyntaxType.FOR -> TODO()
            SyntaxType.TOKEN -> ProcessorToken(syntax, context)
        }
        return processor.process()
    }

    companion object{
        val NOTHING_VALUE = Value(WilesNothing, AbstractType.NOTHING_TYPE, VariableStatus.Const)
    }
}