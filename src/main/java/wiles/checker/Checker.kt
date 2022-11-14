package wiles.checker

import wiles.checker.exceptions.IdentifierExistsException
import wiles.parser.enums.SyntaxType
import wiles.parser.statements.CodeBlockStatement
import wiles.parser.statements.DeclarationStatement
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXISTS_EXCEPTION
import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.constants.Tokens.MUTABLE_ID

class Checker(identifiers: HashMap<Int, String>, idDetailsSet : HashMap<String,IdentifierDetails>) {
    private val identifiers = identifiers.toMutableMap()
    private val idDetailsSet = idDetailsSet.toMutableMap()
    private val exceptions = CompilationExceptionsCollection()

    private fun addIdentifier(component : DeclarationStatement)
    {
        val left = component.left?:return
        val id = left.name
        val location = left.token.location
        if(!identifiers.containsValue(id)) {
            identifiers[identifiers.size] = id
            val isVar = component.name == MUTABLE_ID
            val isInit = component.right != null
            val type = component.typeStatement?.toString() ?: Inferrer.fromDeclaration(component,location)
            idDetailsSet[id] = IdentifierDetails(type,isInit,isVar)
        }
        else throw IdentifierExistsException(IDENTIFIER_EXISTS_EXCEPTION, location)
    }

    fun check(program: CodeBlockStatement): CompilationExceptionsCollection {
        try
        {
            for (component in program.getComponents()) {
                when (component.type) {
                    SyntaxType.METHOD -> TODO()
                    SyntaxType.EXPRESSION -> TODO()
                    SyntaxType.CODE_BLOCK -> TODO()
                    SyntaxType.TOKEN -> TODO()
                    SyntaxType.TYPE -> TODO()
                    SyntaxType.WHEN -> TODO()
                    SyntaxType.DECLARATION -> {
                        if (component is DeclarationStatement)
                            addIdentifier(component)
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
        }
        catch (ex : AbstractCompilationException)
        {
            exceptions.add(ex)
        }
        return exceptions
    }
}