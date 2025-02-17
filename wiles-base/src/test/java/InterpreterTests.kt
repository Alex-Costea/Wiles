
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.interpreter.Interpreter
import wiles.interpreter.Value
import wiles.interpreter.ValuesMap
import wiles.interpreter.types.AbstractType
import wiles.interpreter.types.DecimalType
import wiles.interpreter.types.IntegerType
import wiles.interpreter.types.StringType
import wiles.parser.Parser
import wiles.shared.WilesDecimal
import wiles.shared.constants.Utils
import java.math.BigInteger
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
        getValues("let a := 3").let { values ->
            val obj = intOf(3)
            assertValue(values, "!a") { objValueEquals(it, obj) }
            assertValue(values, "!a") { objTypeEquals(it, IntegerType().singletonValueOf(obj)) }
        }

        getValues("let c := 3.0").let { values ->
            val obj = WilesDecimal("3.0")
            assertValue(values, "!c") { objValueEquals(it, obj) }
            assertValue(values, "!c") { objTypeEquals(it, DecimalType().singletonValueOf(obj)) }
        }

        getValues("""let b := "hello!";""").let { values ->
            val obj = "hello!"
            assertValue(values, "!b") { objValueEquals(it, obj) }
            assertValue(values, "!b") { objTypeEquals(it, StringType().singletonValueOf(obj)) }
        }
    }
}