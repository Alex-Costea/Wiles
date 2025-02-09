package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.TokenExpectedException
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.DeclarationType
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.CANT_BE_EITHER_INTERFACE_OR_OBJECT_ERROR
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.DEFAULT_ID
import wiles.shared.constants.Tokens.TYPE_ID

class DataStatement(context: ParserContext) : AbstractStatement(context) {
    override val syntaxType = SyntaxType.DATA
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    private fun canBeFunction() : Boolean
    {
        var canBeFunction = true
        var canBeObject = true
        for(component in components)
        {
            if(component.name == DEFAULT_ID) {
                canBeObject = false
                continue
            }
            val nonTypeDefComponents = component.getComponents().filter { it.syntaxType != SyntaxType.TYPEDEF }
            if(nonTypeDefComponents.size == 1)
                canBeObject = false
            else canBeFunction = false
        }
        if(!canBeFunction && !canBeObject)
            throw TokenExpectedException(CANT_BE_EITHER_INTERFACE_OR_OBJECT_ERROR, this.getFirstLocation())
        return canBeFunction
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            while(transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.DATA_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                val comp = DeclarationStatement(context, DeclarationType.DATA_PARAM)
                comp.process().throwFirstIfExists()
                components.add(comp)

                if (transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.SEPARATOR_ID)).isEmpty) break
            }
            location = transmitter.expect(ExpectParamsBuilder.tokenOf(Tokens.DATA_END_ID)).location
            if(canBeFunction()) {
                name = TYPE_ID
            }
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}