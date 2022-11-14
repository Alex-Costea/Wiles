package wiles.checker

import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.TokenLocation
import wiles.parser.enums.SyntaxType
import wiles.shared.InternalErrorException
import wiles.parser.statements.CodeBlockStatement
import wiles.parser.statements.DeclarationStatement
import wiles.shared.CompilationExceptionsCollection

class CheckIdentifiers(identifiers: HashMap<Int, String>) {
    private val identifiers = identifiers.toMutableMap()
    private val exceptions = CompilationExceptionsCollection()

    private fun addIdentifier(id : String, location : TokenLocation)
    {
        if(!identifiers.containsValue(id))
            identifiers[identifiers.size] = id
        else exceptions.add(IdentifierExistsException("Identifier is already defined.", location))
    }

    fun check(program: CodeBlockStatement): CompilationExceptionsCollection {
        for(component in program.getComponents())
        {
            when(component.type)
            {
                SyntaxType.METHOD -> TODO()
                SyntaxType.EXPRESSION -> TODO()
                SyntaxType.CODE_BLOCK -> TODO()
                SyntaxType.TOKEN -> TODO()
                SyntaxType.TYPE -> TODO()
                SyntaxType.WHEN -> TODO()
                SyntaxType.DECLARATION ->
                {
                    if(component is DeclarationStatement)
                        addIdentifier(component.left!!.name,component.left!!.token.location)
                    else throw InternalErrorException(INTERNAL_ERROR)
                }
                SyntaxType.RETURN -> TODO()
                SyntaxType.WHILE -> TODO()
                SyntaxType.BREAK -> TODO()
                SyntaxType.CONTINUE -> TODO()
                SyntaxType.METHOD_CALL -> TODO()
                SyntaxType.LIST -> TODO()
                SyntaxType.FOR -> TODO()
            }
        }
        return exceptions
    }
}