
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.interpreter.Interpreter
import wiles.interpreter.Value
import wiles.interpreter.ValuesMap
import wiles.interpreter.errors.IdentifierUnknownException
import wiles.interpreter.types.*
import wiles.parser.Parser
import wiles.shared.TokenLocation
import wiles.shared.WilesDecimal
import wiles.shared.WilesExceptionsCollection
import wiles.shared.constants.Utils
import java.math.BigInteger
import java.util.function.Predicate
import kotlin.test.assertNotNull

@Testable
class InterpreterTests {
    private fun makeInterpreter(code : String) : Interpreter
    {
        val parser = Parser(code, true)
        val results = parser.getResults()
        val syntax = Utils.convertStatementToSyntaxTree(results)
        return Interpreter(null, syntax, true)
    }
    private fun getResults(code : String) : Pair<ValuesMap,WilesExceptionsCollection>
    {
        val interpreter = makeInterpreter(code)
        return Pair(interpreter.getValues(),interpreter.getExceptions())
    }

    private fun assertValue(map : ValuesMap, name : String, predicate : Predicate<Value>)
    {
        val value = map.getOrDefault(name, null)
        assertNotNull(value)
        assert(predicate.test(value))
    }

    private fun intOf(x : Long) = BigInteger.valueOf(x)

    private fun objValueEquals(myValue : Value, compared : Any?): Boolean {
        val obj = myValue.getObj()
        return obj == compared
    }

    private fun objTypeEquals(myValue : Value, compared : AbstractType): Boolean {
        return myValue.getType() == compared
    }

    @Test
    fun declarationsTest()
    {
        getResults("let a := 3").let { (values, exceptions) ->
            val obj = intOf(3)
            assertValue(values, "!a") { objValueEquals(it, obj) }
            assertValue(values, "!a") { objTypeEquals(it, IntegerType().singletonValueOf(obj)) }
            assert(exceptions.isEmpty())
        }

        getResults("let c := 3.0").let { (values, exceptions) ->
            val obj = WilesDecimal("3.0")
            assertValue(values, "!c") { objValueEquals(it, obj) }
            assertValue(values, "!c") { objTypeEquals(it, DecimalType().singletonValueOf(obj)) }
            assert(exceptions.isEmpty())
        }

        getResults("""let b := "hello!";""").let { (values, exceptions) ->
            val obj = "hello!"
            assertValue(values, "!b") { objValueEquals(it, obj) }
            assertValue(values, "!b") { objTypeEquals(it, StringType().singletonValueOf(obj)) }
            assert(exceptions.isEmpty())
        }

        getResults("let a := abc").let{ (values, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == IdentifierUnknownException(
                TokenLocation(1, 10, 1, 13)))
            assertValue(values, "!a") {objTypeEquals(it, InvalidType())}
        }
    }
}