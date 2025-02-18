package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.errors.IdentifierUnknownException
import wiles.processor.types.DecimalType
import wiles.processor.types.IntegerType
import wiles.processor.types.InvalidType
import wiles.processor.types.StringType
import wiles.processor.values.Value
import wiles.processor.values.WilesDecimal
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
            value = Value(decimal, DecimalType(), false)
        }
        else{
            val bigInt = newName.toBigInteger()
            value = Value(bigInt, IntegerType(), false)
        }
    }

    private fun processText(name: String)
    {
        val newName = name.substring(1)
        value = Value(newName, StringType(), false)
    }

    private fun processIdentifier(name: String) {
        value = context.values.getOrDefault(name, null) ?:
            Value(null, InvalidType(), false)
        if(!context.values.containsKey(name))
        {
            val exception = IdentifierUnknownException(syntax.getFirstLocation())
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