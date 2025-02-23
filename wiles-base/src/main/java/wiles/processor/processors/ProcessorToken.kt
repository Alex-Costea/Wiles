package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValueProps.Companion.KNOWN_EXPR
import wiles.processor.errors.IdentifierUnknownException
import wiles.processor.types.DecimalType
import wiles.processor.types.IntegerType
import wiles.processor.types.InvalidType
import wiles.processor.types.StringType
import wiles.processor.values.Value
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
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
            value = Value(decimal, DecimalType().singletonValueOf(decimal), KNOWN_EXPR)
        }
        else{
            val bigInt = WilesInteger(newName)
            value = Value(bigInt, IntegerType().singletonValueOf(bigInt), KNOWN_EXPR)
        }
    }

    private fun processText(name: String)
    {
        val newName = name.substring(1)
        value = Value(newName, StringType().singletonValueOf(newName), KNOWN_EXPR)
    }

    private fun processIdentifier(name: String) {
        value = context.values.getOrDefault(name, null) ?:
            Value(null, InvalidType(), KNOWN_EXPR)
        //TODO: just throw?
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