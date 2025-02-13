package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterContext
import wiles.shared.AbstractSyntaxTree
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.CONST_ID
import wiles.shared.constants.Tokens.GLOBAL_ID
import wiles.shared.constants.Tokens.VARIABLE_ID

class ProcessorDeclaration(
    val syntax : AbstractSyntaxTree,
    val context : InterpreterContext
) : AbstractProcessor {
    override fun process() {
        val details = syntax.details
        if(details.contains(VARIABLE_ID) || details.contains(CONST_ID) || details.contains(GLOBAL_ID))
            TODO("Can't handle these types of declarations yet")
        if(syntax.components[0].syntaxType == SyntaxType.TYPEDEF)
            TODO("No type checking yet!")
        if(syntax.components.last().syntaxType == SyntaxType.EXPRESSION)
            TODO("No expressions yet!")
        val nameToken = syntax.components[0]
        val valueAST = syntax.components[1]
        val processorToken = ProcessorToken(valueAST, context)
        processorToken.process()
        context.values[nameToken.details[0]] = processorToken.value
    }
}