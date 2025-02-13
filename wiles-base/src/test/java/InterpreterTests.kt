
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.interpreter.Interpreter
import wiles.interpreter.ValuesMap
import wiles.parser.Parser
import wiles.shared.constants.Utils

@Testable
class InterpreterTests {
    private fun getValues(code : String): ValuesMap {
        val parser = Parser(code, true)
        val results = parser.getResults()
        val syntax = Utils.convertStatementToSyntaxTree(results)
        val interpreter = Interpreter(null, syntax, true)
        return interpreter.getIdentifiers()
    }

    @Test
    fun test1()
    {
        TODO()
    }
}