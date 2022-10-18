package `in`.costea.wiles.services

import `in`.costea.wiles.commands.AbstractCommand
import `in`.costea.wiles.commands.TokenCommand
import `in`.costea.wiles.commands.expressions.InsideRoundExpressionCommand
import `in`.costea.wiles.statics.Constants.INFIX_OPERATORS
import `in`.costea.wiles.statics.Constants.PRECEDENCE
import `in`.costea.wiles.statics.Constants.PREFIX_OPERATORS
import java.util.*

class OrderOfOperationsProcessor(private val transmitter : TokenTransmitter, private val components: List<AbstractCommand>) {
    //private val newComponents : ArrayList<AbstractCommand> = ArrayList()
    private val minusInfinity = -100
    private var currentPrecedence: Int = minusInfinity

    private fun processStack(stack : LinkedList<AbstractCommand>) : AbstractCommand
    {
        if(stack.isEmpty())
            TODO()
        val current = stack.pop()
        val next  = if(!stack.isEmpty()) {
            processStack(stack)
        } else {
            components.last()
        }
        if(INFIX_OPERATORS.contains(current.name))
        {
            return InsideRoundExpressionCommand(transmitter, listOf(
                components.last(),current,next))
        }
        else if(PREFIX_OPERATORS.contains(current.name))
        {
            return InsideRoundExpressionCommand(transmitter, listOf(
                current,next))
        }
        throw IllegalArgumentException()
    }

    private fun preProcessStack(stack : LinkedList<AbstractCommand>)
    {
        val stack2 : LinkedList<AbstractCommand> = LinkedList()
        while(PRECEDENCE[stack.first.name]!!<currentPrecedence)
            stack2.add(stack.pop())
        val new = processStack(stack)
        stack.clear()
        stack.addAll(stack2)
        stack.add(new)
    }

    fun process(): List<AbstractCommand> {
        val stack : LinkedList<AbstractCommand> = LinkedList()
        var lastOperator : TokenCommand? = null
        for(component in components)
        {
            val content = component.name
            if((component is TokenCommand) &&  (INFIX_OPERATORS.contains(content) || PREFIX_OPERATORS.contains(content)))
            {
                currentPrecedence = PRECEDENCE[component.token.content]!!
                val lastPrecedence = PRECEDENCE[lastOperator?:""]?: minusInfinity
                if(currentPrecedence > lastPrecedence)
                {
                    stack.addLast(component)
                }
                else
                {
                    preProcessStack(stack)
                    stack.addLast(component)
                }
                lastOperator=component
            }
            else stack.addLast(component)
        }
        currentPrecedence = minusInfinity
        preProcessStack(stack)
        return stack.toList()
    }
}