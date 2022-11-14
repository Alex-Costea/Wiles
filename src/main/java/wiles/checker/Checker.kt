package wiles.checker

import wiles.checker.exceptions.IdentifierExistsException
import wiles.checker.exceptions.InvalidIdentifierException
import wiles.parser.data.Token
import wiles.parser.enums.SyntaxType
import wiles.parser.statements.CodeBlockStatement
import wiles.parser.statements.DeclarationStatement
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXISTS_EXCEPTION
import wiles.shared.constants.ErrorMessages.NON_EXISTENT_IDENTIFIER_ERROR
import wiles.shared.constants.ErrorMessages.NON_INIT_IDENTIFIER_ERROR
import wiles.shared.constants.Tokens.MUTABLE_ID

class Checker(identifiers: HashMap<Int, String>, idDetailsSet : HashMap<String,IdentifierDetails>) {
    private val identifiers = identifiers.toMutableMap()
    private val idDetailsSet = idDetailsSet.toMutableMap()
    private val exceptions = CompilationExceptionsCollection()
    private val inferrer = Inferrer(this)

    private fun addIdentifier(component : DeclarationStatement)
    {
        val left = component.left?:return
        val id = left.name
        val location = left.token.location
        if(!identifiers.containsValue(id)) {
            identifiers[identifiers.size] = id
            val isVar = component.name == MUTABLE_ID
            val isInit = component.right != null
            val inferredType = inferrer.fromExpression(component.right,location)
            //TODO: check if matching
            val type = component.typeStatement?.toString() ?: inferredType
            idDetailsSet[id] = IdentifierDetails(type,isInit,isVar)
        }
        else throw IdentifierExistsException(IDENTIFIER_EXISTS_EXCEPTION, location)
    }

    fun getTypeOfIdentifier(token : Token) : String
    {
        val content = token.content
        if(idDetailsSet[content]?.isInit == false)
            throw InvalidIdentifierException(NON_INIT_IDENTIFIER_ERROR, token.location)
        return idDetailsSet[content]?.type ?:
            throw InvalidIdentifierException(NON_EXISTENT_IDENTIFIER_ERROR, token.location)
    }

    fun check(program: CodeBlockStatement): CompilationExceptionsCollection {
        try
        {
            for (component in program.getComponents()) {
                when (component.type) {
                    SyntaxType.METHOD -> TODO()
                    SyntaxType.EXPRESSION -> TODO()
                    SyntaxType.WHEN -> TODO()
                    SyntaxType.WHILE -> TODO()
                    SyntaxType.FOR -> TODO()
                    SyntaxType.RETURN -> TODO()

                    SyntaxType.DECLARATION -> {
                        if (component is DeclarationStatement)
                            addIdentifier(component)
                        else throw InternalErrorException()
                    }

                    SyntaxType.BREAK -> {}
                    SyntaxType.CONTINUE -> {}
                    else -> throw InternalErrorException()
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