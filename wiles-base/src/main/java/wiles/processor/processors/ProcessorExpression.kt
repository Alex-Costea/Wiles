package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.errors.CantBeModifiedException
import wiles.processor.operations.AssignmentOperation
import wiles.processor.operations.PlusOperation
import wiles.processor.values.Value
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.WilesException
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ASSIGN_ID
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
            if(context.exceptions.isNotEmpty())
                return
            try{
                val operand = when(operationType)
                {
                    PLUS_ID -> PlusOperation(left!!, right!!, context)
                    ASSIGN_ID -> {
                        val leftComponent = syntax.components[1]
                        if(leftComponent.syntaxType == SyntaxType.TOKEN) {
                            val name = leftComponent.details[0]
                            if(IS_IDENTIFIER.test(name))
                                AssignmentOperation(left!!, right!!, context, leftComponent)
                            else throw CantBeModifiedException(leftComponent.getFirstLocation())
                        }
                        else TODO("Handling mutable collections")
                    }
                    else -> TODO("Unknown operation")
                }
                value = operand.getNewValue()
            }
            catch (ex : WilesException)
            {
                context.exceptions.add(ex)
            }
        }
    }

}