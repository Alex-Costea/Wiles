
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.parser.Parser
import wiles.processor.Processor
import wiles.processor.data.ValuesMap
import wiles.processor.errors.CantBeModifiedException
import wiles.processor.errors.IdentifierAlreadyDeclaredException
import wiles.processor.errors.IdentifierUnknownException
import wiles.processor.errors.TypeConflictError
import wiles.processor.types.*
import wiles.processor.values.Value
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.shared.TokenLocation
import wiles.shared.WilesExceptionsCollection
import wiles.shared.constants.Utils
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
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objValueEquals(it, obj) }
            assertValue(values, "!a") { objTypeEquals(it, IntegerType()) }
            assert(exceptions.isEmpty())
        }

        getResults("let c := 3.0").let { (values, exceptions) ->
            val obj = WilesDecimal("3.0")
            assertValue(values, "!c") { objValueEquals(it, obj) }
            assertValue(values, "!c") { objTypeEquals(it, DecimalType()) }
            assert(exceptions.isEmpty())
        }

        getResults("""let b := "hello!";""").let { (values, exceptions) ->
            val obj = "hello!"
            assertValue(values, "!b") { objValueEquals(it, obj) }
            assertValue(values, "!b") { objTypeEquals(it, StringType()) }
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
            val obj = WilesInteger(7)
            assertValue(values, "!a") {objValueEquals(it, obj)}
            assertValue(values, "!a") {objTypeEquals(it, IntegerType())}
            assertValue(values, "!b") {objValueEquals(it, obj)}
            assertValue(values, "!b") {objTypeEquals(it, IntegerType())}
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
                val value = WilesInteger(2)
                assertValue(values, "!a") {objValueEquals(it, value)}
                assertValue(values, "!a") { objTypeEquals(it, IntegerType())}
            }
        }
    }

    @Test
    fun expressionsTest()
    {
        getResults("let a := 2 + 3").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val value = WilesInteger(5)
            assertValue(values, "!a"){objValueEquals(it, value)}
            assertValue(values, "!a"){objTypeEquals(it, IntegerType())}
        }
        getResults("""let a := "hello, " + "world!";""").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val value = "hello, world!"
            assertValue(values, "!a"){objValueEquals(it, value)}
            assertValue(values, "!a"){objTypeEquals(it, StringType())}
        }
        getResults("""
            let a := 1 + "a"
            let b := "b" + 2
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a"){objValueEquals(it, "1a")}
            assertValue(values, "!a"){objTypeEquals(it, StringType())}
            assertValue(values, "!b"){objValueEquals(it, "b2")}
            assertValue(values, "!b"){objTypeEquals(it, StringType())}
        }

        getResults("""
            let a := 1 + 2.0
            let b := 1.0 + 3
            let c := 3.0 + 2.0
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a"){objValueEquals(it, WilesDecimal("3.0"))}
            assertValue(values, "!a"){objTypeEquals(it, DecimalType())}
            assertValue(values, "!b"){objValueEquals(it, WilesDecimal("4.0"))}
            assertValue(values, "!b"){objTypeEquals(it, DecimalType())}
            assertValue(values, "!c"){objValueEquals(it, WilesDecimal("5.0"))}
            assertValue(values, "!c"){objTypeEquals(it, DecimalType())}
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
            assertValue(values, "!a") {objValueEquals(it, WilesInteger(3))}
            assertValue(values, "!b") {objValueEquals(it, WilesInteger(2))}
        }

        getResults("a := 123") .let { (_, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == IdentifierUnknownException(
                TokenLocation(1, 1, 1, 2)
            ))
        }

        getResults("""
            let a := 123
            a := 345
        """.trimIndent()). let { (values, exceptions) ->
            assertValue(values, "!a") {objValueEquals(it, WilesInteger(123))}
            assert(exceptions.size == 1)
            assert(exceptions[0] == CantBeModifiedException(TokenLocation(2, 1, 2, 2)))
        }

        getResults("""
            17 := 25
        """.trimIndent()). let { (_, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == CantBeModifiedException(TokenLocation(1, 1, 1, 3)))
        }

        getResults("""
            let var a := 1
            a := "text"
        """.trimIndent()).let{ (values, exceptions) ->
            assertValue(values, "!a") { objValueEquals(it, WilesInteger(1))}
            assert(exceptions.size == 1)
            assert(exceptions[0] == TypeConflictError( IntegerType(), StringType().singletonValueOf("text"),
                TokenLocation(2, 1, 2, 2)))
        }

    }

    @Test
    fun standardLibraryTest()
    {
        getResults("").let{ (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!true"){objValueEquals(it, true)}
            assertValue(values, "!true"){objTypeEquals(it, BooleanType())}

            assertValue(values, "!false"){objValueEquals(it, false)}
            assertValue(values, "!false"){objTypeEquals(it, BooleanType())}

            assertValue(values, "!nothing"){objValueEquals(it, null)}
            assertValue(values, "!nothing"){objTypeEquals(it, NothingType())}

            assertValue(values, "!int"){objValueEquals(it, IntegerType())}
            assertValue(values, "!int"){objTypeEquals(it, TypeType())}

            assertValue(values, "!text"){objValueEquals(it, StringType())}
            assertValue(values, "!text"){objTypeEquals(it, TypeType())}

            assertValue(values, "!decimal"){objValueEquals(it, DecimalType())}
            assertValue(values, "!decimal"){objTypeEquals(it, TypeType())}
        }
    }
}