
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.interpreter.Interpreter
import wiles.interpreter.Value
import wiles.interpreter.ValuesMap
import wiles.parser.Parser
import wiles.shared.constants.Utils
import java.util.function.Predicate
import kotlin.test.assertNotNull

@Testable
class InterpreterTests {
    private fun getValues(code : String): ValuesMap {
        val parser = Parser(code, true)
        val results = parser.getResults()
        val syntax = Utils.convertStatementToSyntaxTree(results)
        val interpreter = Interpreter(null, syntax, true)
        return interpreter.getValues()
    }

    private fun assertValue(map : ValuesMap, name : String, predicate : Predicate<Value>)
    {
        val value = map.getOrDefault(name, null)
        assertNotNull(value)
        assert(predicate.test(value))
    }

    @Test
    fun test1()
    {
        val values = getValues("let a := 3")
        assertValue(values, "!a") { it.getObj() == 3 }
    }
}