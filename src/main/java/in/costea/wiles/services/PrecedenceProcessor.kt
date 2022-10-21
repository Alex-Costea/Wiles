package `in`.costea.wiles.services

import `in`.costea.wiles.commands.AbstractCommand
import `in`.costea.wiles.commands.TokenCommand
import `in`.costea.wiles.commands.expressions.BinaryExpressionCommand
import `in`.costea.wiles.statics.Constants.INFIX_OPERATORS
import `in`.costea.wiles.statics.Constants.OPERATOR_EXPECTED_ERROR
import `in`.costea.wiles.statics.Constants.PRECEDENCE
import `in`.costea.wiles.statics.Constants.PREFIX_OPERATORS
import `in`.costea.wiles.statics.Constants.RIGHT_TO_LEFT
import java.lang.Byte.MIN_VALUE
import java.util.*

class PrecedenceProcessor(private val transmitter: TokenTransmitter) {

    private val stack : LinkedList<AbstractCommand> = LinkedList()
    private fun isOperator(content : String) = (INFIX_OPERATORS.contains(content) || PREFIX_OPERATORS.contains(content))

    private fun checkPrecedence(currentPrecedence: Byte,lastPrecedence : Byte) =
        currentPrecedence < lastPrecedence
                || (currentPrecedence==lastPrecedence && !RIGHT_TO_LEFT.contains(currentPrecedence))

    private fun processStack(currentPrecedence : Byte)
    {
        var token1 : AbstractCommand? = null
        val token2 = stack.removeLast()
        if(stack.isEmpty())
            return
        val operation = stack.removeLast() as TokenCommand
        if(!isOperator(operation.name))
            throw java.lang.IllegalStateException()
        if(INFIX_OPERATORS.contains(operation.name))
            token1=stack.removeLast()

        val lastPrecedence : Byte = if(stack.isEmpty()) MIN_VALUE
            else if(isOperator(stack.last.name)) PRECEDENCE[stack[stack.lastIndex].name]!!
        else throw IllegalStateException()

        if(token1 == null)
            stack.add(BinaryExpressionCommand(transmitter, operation,null,token2))
        else stack.add(BinaryExpressionCommand(transmitter,operation,token1,token2))

        if(stack.size == 1)
            return

        if(checkPrecedence(currentPrecedence, lastPrecedence))
            processStack(currentPrecedence)
    }

    private fun handleComponent(component : TokenCommand?)
    {
        val currentPrecedence = PRECEDENCE[component?.name]?: MIN_VALUE
        val lastPrecedence : Byte = if(stack.size <= 1)
            return
        else if(isOperator(stack.last.name))
            PRECEDENCE[stack.last.name]!!
        else if(isOperator(stack[stack.lastIndex-1].name))
            PRECEDENCE[stack[stack.lastIndex-1].name]!!
        else throw IllegalStateException(OPERATOR_EXPECTED_ERROR)
        if(!PREFIX_OPERATORS.contains(component?.name) && checkPrecedence(currentPrecedence, lastPrecedence)) {
            processStack(currentPrecedence)
        }
    }

    fun add(component: AbstractCommand) {
        if(component is TokenCommand && isOperator(component.name))
        {
            handleComponent(component)
        }
        stack.addLast(component)
    }

    fun getResult() : AbstractCommand
    {
        handleComponent(null)
        assert(stack.size==1)
        if(stack.size == 1 && stack.last is TokenCommand)
            return BinaryExpressionCommand(transmitter,null,null,stack.last)
        return stack.pop()
    }
}