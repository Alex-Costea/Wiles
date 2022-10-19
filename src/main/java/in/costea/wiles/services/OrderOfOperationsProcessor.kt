package `in`.costea.wiles.services

import `in`.costea.wiles.commands.AbstractCommand
import `in`.costea.wiles.commands.expressions.BinaryExpressionCommand
import `in`.costea.wiles.statics.Constants.INFIX_OPERATORS
import `in`.costea.wiles.statics.Constants.PRECEDENCE
import `in`.costea.wiles.statics.Constants.PREFIX_OPERATORS
import `in`.costea.wiles.statics.Constants.RIGHT_TO_LEFT
import java.lang.Integer.MIN_VALUE
import java.util.*

class OrderOfOperationsProcessor(private val transmitter : TokenTransmitter, private val components: List<AbstractCommand>) {
    private fun isOperator(content : String) = (INFIX_OPERATORS.contains(content) || PREFIX_OPERATORS.contains(content))


    private fun checkPrecedence(currentPrecedence: Int,lastPrecedence : Int) =
        currentPrecedence < lastPrecedence || (currentPrecedence==lastPrecedence && !RIGHT_TO_LEFT.contains(currentPrecedence))

    private fun processStack(stack: LinkedList<AbstractCommand>, currentPrecedence : Int)
    {
        var token1 : AbstractCommand? = null
        val token2 = stack.removeLast()
        if(stack.isEmpty())
            return
        val operation = stack.removeLast()
        if(!isOperator(operation.name))
            throw IllegalStateException("Operator expected!")
        if(INFIX_OPERATORS.contains(operation.name))
            token1=stack.removeLast()

        val lastPrecedence : Int = if(stack.isEmpty()) MIN_VALUE
            else if(isOperator(stack.last.name)) PRECEDENCE[stack[stack.lastIndex].name]!!
        else throw IllegalStateException()

        if(token1 == null)
            stack.add(BinaryExpressionCommand(transmitter, listOf(operation, token2)))
        else stack.add(BinaryExpressionCommand(transmitter,listOf(token1,operation,token2)))

        if(stack.size == 1)
            return

        if(checkPrecedence(currentPrecedence, lastPrecedence))
            processStack(stack,currentPrecedence)
    }

    private fun handleComponent(component : AbstractCommand?, stack: LinkedList<AbstractCommand>)
    {
        val currentPrecedence = PRECEDENCE[component?.name]?: MIN_VALUE
        val lastPrecedence : Int = if(stack.size <= 1)
            return
        else if(isOperator(stack.last.name))
            PRECEDENCE[stack.last.name]!!
        else if(isOperator(stack[stack.lastIndex-1].name))
            PRECEDENCE[stack[stack.lastIndex-1].name]!!
        else throw IllegalStateException("Operator expected")
        if(checkPrecedence(currentPrecedence, lastPrecedence)) {
            processStack(stack,currentPrecedence)
        }
    }

    fun process(): List<AbstractCommand> {
        val stack : LinkedList<AbstractCommand> = LinkedList()
        for(component in components)
        {
            if(isOperator(component.name))
            {
                handleComponent(component, stack)
            }
            stack.addLast(component)
        }
        handleComponent(null,stack)
        return stack.toList()
    }
}