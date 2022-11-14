package wiles.checker

import wiles.parser.statements.CodeBlockStatement
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import wiles.shared.constants.Types.BOOLEAN_ID
import wiles.shared.constants.Types.STRING_ID

class CheckerProcessor(program: CodeBlockStatement) {
    val exceptions = CompilationExceptionsCollection()
    companion object
    {
        private val defaultIdentifiers = HashMap<Int, String>()
        init {
            defaultIdentifiers[-1] = NOTHING_ID
            defaultIdentifiers[0] = FALSE_ID
            defaultIdentifiers[1] = TRUE_ID
            defaultIdentifiers[2] = "!writeline"
            defaultIdentifiers[10] = "INT64.as_text"
        }

        private val idDetailsSet = HashMap<String,IdentifierDetails>()
        init {
            idDetailsSet["!writeline"] = IdentifierDetails("TYPE METHOD(METHOD(DECLARATION ANON_ARG(TYPE STRING; !text)))")
            idDetailsSet["INT64.as_text"] = IdentifierDetails("TYPE $STRING_ID")
            idDetailsSet[NOTHING_ID] = IdentifierDetails("TYPE $NOTHING_ID")
            idDetailsSet[FALSE_ID] = IdentifierDetails("TYPE $BOOLEAN_ID")
            idDetailsSet[TRUE_ID] = IdentifierDetails("TYPE $BOOLEAN_ID")
        }
    }
    init {
        exceptions.addAll(Checker(defaultIdentifiers, idDetailsSet).check(program))
    }
}