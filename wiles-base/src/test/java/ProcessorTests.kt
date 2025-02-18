
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.parser.Parser
import wiles.processor.Processor
import wiles.processor.data.ValuesMap
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.IdentifierUnknownException
import wiles.processor.types.*
import wiles.processor.values.Value
import wiles.processor.values.WilesDecimal
import wiles.shared.TokenLocation
import wiles.shared.WilesExceptionsCollection
import wiles.shared.constants.Utils
import java.math.BigInteger
import java.util.function.Predicate
import kotlin.test.assertNotNull

@Testable
class ProcessorTests {
    private fun makeInterpreter(code : String) : Processor
    {
        val parser = Parser(code, true)
        val results = parser.getResults()
        val syntax = Utils.convertStatementToSyntaxTree(results)
        return Processor(null, syntax, true)
    }
    private fun getResults(code : String) : Pair<ValuesMap,WilesExceptionsCollection>
    {
        val interpreter = makeInterpreter(code)
        interpreter.process()
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
    fun basicDeclarationsTest()
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

        getResults("""
            let a := 7
            let b := a
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val obj = intOf(7)
            val type = IntegerType().singletonValueOf(obj)
            assertValue(values, "!a") {objValueEquals(it, obj)}
            assertValue(values, "!a") {objTypeEquals(it, type)}
            assertValue(values, "!b") {objValueEquals(it, obj)}
            assertValue(values, "!b") {objTypeEquals(it, type)}
            assert(values["!a"] == values["!b"])
        }

        getResults("""
            let a := 2
            let a := 3
        """.trimIndent()).let { (values, exceptions) ->
            {
                assert(exceptions.size == 1)
                assert(exceptions[0] === IdentifierAlreadyDeclaredException(
                    TokenLocation(1, 4, 1, 5)))
                val value = intOf(2)
                val type = IntegerType().singletonValueOf(value)
                assertValue(values, "!a") {objValueEquals(it, value)}
                assertValue(values, "!a") { objTypeEquals(it, type)}
            }
        }
    }

    @Test
    fun expressionsTest()
    {
        getResults("let a := 2 + 3").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val value = intOf(5)
            val type = IntegerType().singletonValueOf(value)
            assertValue(values, "!a"){objValueEquals(it, value)}
            assertValue(values, "!a"){objTypeEquals(it, type)}
        }
    }

    @Test
    fun assignmentTest()
    {
        getResults("""
            let var a := 2
            let b := a
            a := 3
        """.trimIndent()). let{(values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a") {objValueEquals(it, intOf(3))}
            assertValue(values, "!b") {objValueEquals(it, intOf(2))}
        }

        getResults("a := 123") .let { (values, exceptions) ->
            assert(exceptions.size == 1)
            assert(values.isEmpty())
            assert(exceptions[0] == IdentifierUnknownException(
                TokenLocation(1, 1, 1, 2)))
        }
    }
}