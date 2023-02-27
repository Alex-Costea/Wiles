package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.AS_ID

class WithStatement(context: Context) : AbstractStatement(context) {
    private val expression = DefaultExpression(context)
    private val typeStatement = TypeDefinitionStatement(context)
    private val codeBlock = CodeBlockStatement(context)
    override val type: SyntaxType
        get() = SyntaxType.WITH

    private val components = mutableListOf(typeStatement,expression,codeBlock)

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try
        {
            exceptions.addAll(expression.process())
            transmitter.expect(tokenOf(AS_ID))
            exceptions.addAll(typeStatement.process())
            exceptions.addAll(codeBlock.process())
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }
}