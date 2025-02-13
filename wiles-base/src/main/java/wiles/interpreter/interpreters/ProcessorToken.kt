package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterContext
import wiles.interpreter.Value
import wiles.interpreter.WilesType
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType

class ProcessorToken(
    val syntax : AbstractSyntaxTree,
    val context : InterpreterContext
) : AbstractProcessor {
    lateinit var value : Value

    private fun processNr(name: String)
    {
        val newName = name.substring(1).replace("_","")
        if(newName.contains("."))
            TODO("No rationals yet")
        value = Value(newName.toBigInteger(), WilesType())
    }

    override fun process() {
        assert(syntax.syntaxType == SyntaxType.TOKEN)
        val name = syntax.details[0]
        if(name.startsWith("#"))
            processNr(name)
        else TODO("Can only handle nr")
    }
}