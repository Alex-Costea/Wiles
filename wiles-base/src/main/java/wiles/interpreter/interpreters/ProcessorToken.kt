package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.errors.IdentifierUnknownException
import wiles.interpreter.types.DecimalType
import wiles.interpreter.types.IntegerType
import wiles.interpreter.types.InvalidType
import wiles.interpreter.types.StringType
import wiles.interpreter.values.Value
import wiles.interpreter.values.WilesDecimal
import wiles.shared.AbstractSyntaxTree
import wiles.shared.InternalErrorException
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Predicates.IS_LITERAL
import wiles.shared.constants.Predicates.IS_NUMBER_LITERAL
import wiles.shared.constants.Predicates.IS_TEXT_LITERAL

class ProcessorToken(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    lateinit var value : Value

    private fun processNr(name: String)
    {
        val newName = name.substring(1).replace("_","")
        if(newName.contains("."))
        {
            val decimal = WilesDecimal(newName)
            value = Value(decimal, DecimalType().singletonValueOf(decimal), null)
        }
        else{
            val bigInt = newName.toBigInteger()
            value = Value(bigInt, IntegerType().singletonValueOf(bigInt), null)
        }
    }

    private fun processText(name: String)
    {
        val newName = name.substring(1)
        value = Value(newName, StringType().singletonValueOf(newName), null)
    }

    private fun processIdentifier(name: String) {
        value = context.values.getOrDefault(name, null) ?:
            Value(null, InvalidType(), null)
        if(!context.values.containsKey(name))
        {
            val exception = IdentifierUnknownException(syntax.location!!)
            context.exceptions.add(exception)
        }
    }


    override fun process() {
        assert(syntax.syntaxType == SyntaxType.TOKEN)
        val name = syntax.details[0]
        assert(IS_LITERAL.test(name))
        if(IS_NUMBER_LITERAL.test(name))
            processNr(name)
        else if(IS_TEXT_LITERAL.test(name))
            processText(name)
        else if(IS_IDENTIFIER.test(name))
            processIdentifier(name)
        else throw InternalErrorException()
    }
}