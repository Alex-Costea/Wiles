package `in`.costea.wiles.services

import `in`.costea.wiles.commands.AbstractCommand
import `in`.costea.wiles.commands.TokenCommand
import `in`.costea.wiles.commands.expressions.BinaryExpressionCommand
import `in`.costea.wiles.statics.Constants.INFIX_OPERATORS
import `in`.costea.wiles.statics.Constants.PRECEDENCE
import `in`.costea.wiles.statics.Constants.PREFIX_OPERATORS
import java.lang.Integer.MAX_VALUE
import java.lang.Integer.MIN_VALUE
import java.util.*

class OrderOfOperationsProcessor(private val transmitter : TokenTransmitter, private val components: List<AbstractCommand>) {
    private fun isOperator(content : String) = (INFIX_OPERATORS.contains(content) || PREFIX_OPERATORS.contains(content))

    private fun createCommand(stack : LinkedList<AbstractCommand>) : BinaryExpressionCommand
    {
        if(stack.isEmpty())
            throw IllegalArgumentException("Stack cannot be empty!")
        var previous : AbstractCommand? = null
        var current = stack.pop()
        if(!isOperator(current.name))
        {
            previous = current
            current = stack.pop()
        }
        val next  = if(stack.size>1) createCommand(stack) else stack.pop()
        previous ?: return BinaryExpressionCommand(transmitter, listOf(current,next))
        return BinaryExpressionCommand(transmitter, listOf(previous,current,next))
    }

    private fun processStack(stack : LinkedList<AbstractCommand>, currentPrecedence : Int)
    {
        val stack2 : LinkedList<AbstractCommand> = LinkedList()
        while((PRECEDENCE[stack.first.name] ?: MAX_VALUE) < currentPrecedence)
            stack2.add(stack.pop())
        val new = createCommand(stack)
        stack.clear()
        stack.addAll(stack2)
        stack.add(new)
    }

    fun process(): List<AbstractCommand> {
        val stack : LinkedList<AbstractCommand> = LinkedList()
        var lastOperator : TokenCommand? = null
        var currentPrecedence: Int
        for(component in components)
        {
            if((component is TokenCommand) && isOperator(component.name))
            {
                currentPrecedence = PRECEDENCE[component.token.content]!!
                val lastPrecedence = PRECEDENCE[lastOperator?.name?:""]?: MIN_VALUE
                if (currentPrecedence <= lastPrecedence) {
                    processStack(stack,currentPrecedence)
                }
                lastOperator=component
            }
            stack.addLast(component)
        }
        processStack(stack, MIN_VALUE)
        return stack.toList()
    }
}