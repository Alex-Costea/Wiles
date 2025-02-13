package wiles.parser.services

import wiles.parser.builders.ParserContext
import wiles.shared.AbstractStatement
import wiles.parser.statements.TokenStatement
import wiles.parser.statements.expressions.BinaryExpression
import wiles.shared.InternalErrorException
import wiles.shared.constants.ErrorMessages.OPERATOR_EXPECTED_ERROR
import wiles.shared.constants.Precedence.PRECEDENCE
import wiles.shared.constants.Precedence.RIGHT_TO_LEFT
import wiles.shared.constants.Tokens.ALL_OPERATORS
import wiles.shared.constants.Tokens.INFIX_OPERATORS
import wiles.shared.constants.Tokens.PREFIX_OPERATORS
import java.lang.Byte.MIN_VALUE
import java.util.*

class PrecedenceProcessor(private val context : ParserContext) {

    private val stack : LinkedList<AbstractStatement> = LinkedList()
    private fun isOperator(content : String) = (ALL_OPERATORS.contains(content))

    private fun checkPrecedence(currentPrecedence: Byte,lastPrecedence : Byte) =
        currentPrecedence < lastPrecedence
                || (currentPrecedence==lastPrecedence && !RIGHT_TO_LEFT.contains(currentPrecedence))

    private fun processStack(currentPrecedence : Byte)
    {
        var token1 : AbstractStatement? = null
        var token2 = stack.removeLast()
        if(stack.isEmpty())
            return
        var operation = stack.removeLast()
        if(isOperator(token2.name))
        {
            operation = token2.also { token2 = operation } as TokenStatement
        }
        else operation = operation as TokenStatement
        if(INFIX_OPERATORS.contains(operation.name))
            token1=stack.removeLast()

        val lastPrecedence : Byte = if(stack.isEmpty()) MIN_VALUE
            else if(isOperator(stack.last.name)) PRECEDENCE[stack[stack.lastIndex].name]!!
        else throw InternalErrorException()

        if(token1 == null)
            stack.add(BinaryExpression(operation,null,token2,context))
        else stack.add(BinaryExpression(operation,token1,token2,context))

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
            return BinaryExpression(null,null,stack.last, context)
        return stack.pop()
    }
}