package wiles.interpreter.interpreters

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.values.Value
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType

class ProcessorExpression(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
): AbstractProcessor(syntax, context){

    lateinit var value : Value

    override fun process() {
        if(syntax.syntaxType == SyntaxType.TOKEN)
        {
            val processorToken = ProcessorToken(syntax, context)
            processorToken.process()
            value = processorToken.value
        }
        else{
            TODO("Expressions not yet implemented.")
        }
    }

}