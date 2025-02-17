package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.operations.PlusOperation
import wiles.interpreter.values.Value
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.PLUS_ID

class ProcessorExpression(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
): AbstractProcessor(syntax, context){

    lateinit var value : Value

    fun getValue(tree : AbstractSyntaxTree?): Value? {
        if(tree == null) return null
        val innerProcessorExpression = ProcessorExpression(tree, context)
        innerProcessorExpression.process()
        return innerProcessorExpression.value
    }

    override fun process() {

        if(syntax.syntaxType == SyntaxType.TOKEN)
        {
            val processorToken = ProcessorToken(syntax, context)
            processorToken.process()
            value = processorToken.value
        }
        else{
            val operationType = syntax.components[0].details[0]
            val left = getValue(syntax.components.getOrNull(1))
            val right = getValue(syntax.components.getOrNull(2))
            val operand = when(operationType)
            {
                PLUS_ID -> PlusOperation(left!!, right!!, context)
                else -> TODO("Unknown operation")
            }
            value = operand.getNewValue()
        }
    }

}