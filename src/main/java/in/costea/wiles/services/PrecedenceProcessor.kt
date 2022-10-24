package `in`.costea.wiles.services

import `in`.costea.wiles.statements.AbstractStatement
import `in`.costea.wiles.statements.TokenStatement
import `in`.costea.wiles.statements.expressions.BinaryExpression
import `in`.costea.wiles.constants.Tokens.INFIX_OPERATORS
import `in`.costea.wiles.constants.ErrorMessages.OPERATOR_EXPECTED_ERROR
import `in`.costea.wiles.constants.Precedence.PRECEDENCE
import `in`.costea.wiles.constants.Tokens.PREFIX_OPERATORS
import `in`.costea.wiles.constants.Precedence.RIGHT_TO_LEFT
import `in`.costea.wiles.exceptions.InternalErrorException
import java.lang.Byte.MIN_VALUE
import java.util.*

class PrecedenceProcessor(private val transmitter: TokenTransmitter) {

    private val stack : LinkedList<AbstractStatement> = LinkedList()
    private fun isOperator(content : String) = (INFIX_OPERATORS.contains(content) || PREFIX_OPERATORS.contains(content))

    private fun checkPrecedence(currentPrecedence: Byte,lastPrecedence : Byte) =
        currentPrecedence < lastPrecedence
                || (currentPrecedence==lastPrecedence && !RIGHT_TO_LEFT.contains(currentPrecedence))

    private fun processStack(currentPrecedence : Byte)
    {
        var token1 : AbstractStatement? = null
        val token2 = stack.removeLast()
        if(stack.isEmpty())
            return
        val operation = stack.removeLast() as TokenStatement
        if(!isOperator(operation.name))
            throw InternalErrorException()
        if(INFIX_OPERATORS.contains(operation.name))
            token1=stack.removeLast()

        val lastPrecedence : Byte = if(stack.isEmpty()) MIN_VALUE
            else if(isOperator(stack.last.name)) PRECEDENCE[stack[stack.lastIndex].name]!!
        else throw InternalErrorException()

        if(token1 == null)
            stack.add(BinaryExpression(transmitter, operation,null,token2))
        else stack.add(BinaryExpression(transmitter,operation,token1,token2))

        if(stack.size == 1)
            return

        if(checkPrecedence(currentPrecedence, lastPrecedence))
            processStack(currentPrecedence)
    }

    private fun handleComponent(component : TokenStatement?)
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

    fun add(component: AbstractStatement) {
        if(component is TokenStatement && isOperator(component.name))
        {
            handleComponent(component)
        }
        stack.addLast(component)
    }

    fun getResult() : AbstractStatement
    {
        handleComponent(null)
        assert(stack.size==1)
        if(stack.size == 1 && stack.last is TokenStatement)
            return BinaryExpression(transmitter,null,null,stack.last)
        return stack.pop()
    }
}