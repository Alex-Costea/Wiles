package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterContext
import wiles.interpreter.Value
import wiles.interpreter.types.DecimalType
import wiles.interpreter.types.IntegerType
import wiles.interpreter.types.StringType
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.WilesDecimal

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
            value = Value(decimal, DecimalType().singletonValueOf(decimal))
        }
        else{
            val bigInt = newName.toBigInteger()
            value = Value(bigInt, IntegerType().singletonValueOf(bigInt))
        }
    }

    private fun processText(name: String)
    {
        val newName = name.substring(1)
        value = Value(newName, StringType().singletonValueOf(newName))
    }

    override fun process() {
        assert(syntax.syntaxType == SyntaxType.TOKEN)
        val name = syntax.details[0]
        if(name.startsWith("#"))
            processNr(name)
        else if(name.startsWith("@"))
            processText(name)
        else TODO("Can't handle this token type")
    }
}