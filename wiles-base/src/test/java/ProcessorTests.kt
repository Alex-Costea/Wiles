
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.parser.Parser
import wiles.processor.Processor
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.errors.*
import wiles.processor.types.*
import wiles.processor.types.AbstractType.Companion.BOOLEAN_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INTEGER_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesNothing
import wiles.shared.data.TokenLocation
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.constants.Utils
import java.util.*
import java.util.function.Predicate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Testable
class ProcessorTests {
    private fun makeInterpreter(code : String, scanner: Scanner?) : Processor
    {
        val parser = Parser(code, true)
        val results = parser.getResults()
        val syntax = Utils.convertStatementToSyntaxTree(results)
        return Processor(scanner, syntax, true)
    }

    private fun getCompilationResults(code : String) : Pair<ValuesMap, WilesExceptionsCollection>
    {
        val interpreter = makeInterpreter(code, null)
        interpreter.process()
        return Pair(interpreter.getValues(),interpreter.getExceptions())
    }

    private fun getRunningResults(code : String, input : String = "") : Pair<ValuesMap, WilesExceptionsCollection>
    {
        val interpreter = makeInterpreter(code, Scanner(input))
        interpreter.process()
        return Pair(interpreter.getValues(),interpreter.getExceptions())
    }

    private fun assertValue(map : ValuesMap, name : String, predicate : Predicate<Value>)
    {
        val value = map[name]
        assertNotNull(value)
        assert(predicate.test(value))
    }

    private fun valueEquals(myValue : Value, compared : Any?): Boolean {
        return myValue.getObj() == compared
    }

    private fun typeEquals(myValue : Value, compared : AbstractType): Boolean {
        return myValue.getType() == compared
    }

    @Test
    fun basicDeclarationsTest()
    {
        getCompilationResults("let a := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { valueEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INTEGER_TYPE.exactly(obj)) }
            assert(exceptions.isEmpty())
        }

        getCompilationResults("let c := 3.0").let { (values, exceptions) ->
            val obj = WilesDecimal("3.0")
            assertValue(values, "!c") { valueEquals(it, obj) }
            assertValue(values, "!c") { typeEquals(it, DECIMAL_TYPE.exactly(obj)) }
            assert(exceptions.isEmpty())
        }

        getCompilationResults("""let b := "hello!";""").let { (values, exceptions) ->
            val obj = "hello!"
            assertValue(values, "!b") { valueEquals(it, obj) }
            assertValue(values, "!b") { typeEquals(it, TEXT_TYPE.exactly(obj)) }
            assert(exceptions.isEmpty())
        }

        getCompilationResults("let a := abc").let{ (_, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == IdentifierUnknownException(
                TokenLocation(1, 10, 1, 13)
            ))
        }

        getCompilationResults("""
            let a := 7
            let b := a
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val obj = WilesInteger(7)
            assertValue(values, "!a") {valueEquals(it, obj)}
            assertValue(values, "!a") {typeEquals(it, INTEGER_TYPE.exactly(obj))}
            assertValue(values, "!b") {valueEquals(it, obj)}
            assertValue(values, "!b") {typeEquals(it, INTEGER_TYPE.exactly(obj))}
            assert(values["!a"] == values["!b"])
        }

        getCompilationResults("""
            let a := 2
            let a := 3
        """.trimIndent()).let { (values, exceptions) ->
            {
                assert(exceptions.size == 1)
                assert(exceptions[0] === IdentifierAlreadyDeclaredException(
                    TokenLocation(1, 4, 1, 5)
                ))
                val value = WilesInteger(2)
                assertValue(values, "!a") {valueEquals(it, value)}
                assertValue(values, "!a") { typeEquals(it, INTEGER_TYPE)}
            }
        }

        getCompilationResults("let const a := rand()").let { (_, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == ValueNotConstException(
                TokenLocation(1, 11, 1, 12)
            ))
        }
    }

    @Test
    fun typeDefTests(){
        getCompilationResults("let a : Int := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { valueEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INTEGER_TYPE) }
            assert(exceptions.isEmpty())
        }

        getCompilationResults("let a : Anything := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { valueEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, AnythingType()) }
            assert(exceptions.isEmpty())
        }

        getCompilationResults("let a : Text := 3").let { (_, exceptions) ->
            assert(exceptions.size == 1)
            assertEquals(exceptions[0], TypeConflictError(TEXT_TYPE,INTEGER_TYPE.exactly(WilesInteger(3)),
                TokenLocation(1, 9, 1, 13)
            ))
        }

        getCompilationResults("let a : 123 := 123").let { (values, exceptions) ->
            val obj = WilesInteger(123)
            assertValue(values, "!a") { valueEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INTEGER_TYPE.exactly(obj)) }
            assert(exceptions.isEmpty())
        }

        getCompilationResults("""
            let a := rand()
            let b : a := 0.5
        """.trimIndent()).let { (_, exceptions) ->
            {
                assert(exceptions.size == 1)
                assertEquals(exceptions[0], ValueNotConstException(
                    TokenLocation(2, 9, 2, 10)
                ))
            }
        }
    }

    @Test
    fun expressionsTest()
    {
        getCompilationResults("let a := 2 + 3").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val value = WilesInteger(5)
            assertValue(values, "!a"){valueEquals(it, value)}
            assertValue(values, "!a"){typeEquals(it, INTEGER_TYPE.exactly(value))}
        }
        getCompilationResults("""let a := "hello, " + "world!";""").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val value = "hello, world!"
            assertValue(values, "!a"){valueEquals(it, value)}
            assertValue(values, "!a"){typeEquals(it, TEXT_TYPE.exactly(value))}
        }
        getCompilationResults("""
            let a := 1 + "a"
            let b := "b" + 2
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a"){valueEquals(it, "1a")}
            assertValue(values, "!a"){typeEquals(it, TEXT_TYPE.exactly("1a"))}
            assertValue(values, "!b"){valueEquals(it, "b2")}
            assertValue(values, "!b"){typeEquals(it, TEXT_TYPE.exactly("b2"))}
        }

        getCompilationResults("""
            let a := 1 + 2.0
            let b := 1.0 + 3
            let c := 3.0 + 2.0
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a"){valueEquals(it, WilesDecimal("3.0"))}
            assertValue(values, "!a"){typeEquals(it, DECIMAL_TYPE.exactly(WilesDecimal("3.0")))}
            assertValue(values, "!b"){valueEquals(it, WilesDecimal("4.0"))}
            assertValue(values, "!b"){typeEquals(it, DECIMAL_TYPE.exactly(WilesDecimal("4.0")))}
            assertValue(values, "!c"){valueEquals(it, WilesDecimal("5.0"))}
            assertValue(values, "!c"){typeEquals(it, DECIMAL_TYPE.exactly(WilesDecimal("5.0")))}
        }
    }

    @Test
    fun assignmentTest()
    {
        getCompilationResults("""
            let var a := 2
            let b := a
            a := 3
        """.trimIndent()). let{(values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a") {valueEquals(it, WilesInteger(3))}
            assertValue(values, "!a") {typeEquals(it, INTEGER_TYPE.exactly(WilesInteger(3)))}
            assertValue(values, "!b") {valueEquals(it, WilesInteger(2))}
            assertValue(values, "!b") {typeEquals(it, INTEGER_TYPE)}
        }

        getCompilationResults("a := 123") .let { (_, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == IdentifierUnknownException(
                TokenLocation(1, 1, 1, 2)
            ))
        }

        getCompilationResults("""
            let a := 123
            a := 345
        """.trimIndent()). let { (values, exceptions) ->
            assertValue(values, "!a") {valueEquals(it, WilesInteger(123))}
            assert(exceptions.size == 1)
            assert(exceptions[0] == CantBeModifiedException(TokenLocation(2, 1, 2, 2)))
        }

        getCompilationResults("""
            17 := 25
        """.trimIndent()). let { (_, exceptions) ->
            assert(exceptions.size == 1)
            assert(exceptions[0] == CantBeModifiedException(TokenLocation(1, 1, 1, 3)))
        }

        getCompilationResults("""
            let var a := 1
            a := "text"
        """.trimIndent()).let{ (values, exceptions) ->
            assertValue(values, "!a") { valueEquals(it, WilesInteger(1))}
            assert(exceptions.size == 1)
            assert(exceptions[0] == TypeConflictError( INTEGER_TYPE, TEXT_TYPE.exactly("text"),
                TokenLocation(2, 1, 2, 2)
            ))
        }

    }

    @Test
    fun randTest()
    {
        getRunningResults("let a := rand()").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a") {it.getObj() is WilesDecimal}
            assertValue(values, "!a") {(it.getObj() as WilesDecimal).toString().startsWith("0.")}
            assertValue(values, "!a") {(it.getType() is DecimalType) && it.getType().getValue() == it.getObj()}
        }

        getCompilationResults("let a := rand() + 4").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a") {valueEquals(it, null)}
            assertValue(values, "!a") {typeEquals(it, DECIMAL_TYPE)}
        }

        getRunningResults("let a := rand() + 4").let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!a") {it.getObj() is WilesDecimal}
            assertValue(values, "!a") {(it.getObj() as WilesDecimal).toString()[0] == '4'}
            assertValue(values, "!a") {(it.getType() is DecimalType) && it.getType().getValue() == it.getObj()}
        }
    }

    @Test
    fun levelScopeTest()
    {
        getCompilationResults("""
            let b := a + 5
            def a : Int := 123
        """.trimIndent()).let { (values, exceptions) ->
            assert(exceptions.isEmpty())
            val value123 = WilesInteger(123)
            val value128 = WilesInteger(128)
            assertValue(values, "!a"){valueEquals(it, value123)}
            assertValue(values, "!a"){typeEquals(it, INTEGER_TYPE)}
            assertValue(values, "!b"){valueEquals(it, value128)}
            assertValue(values, "!b"){typeEquals(it, INTEGER_TYPE.exactly(value128))}
        }

        getCompilationResults("def a : Int := true"). let{ (_, exceptions) ->
            assert(exceptions.size == 1)
            assertEquals(exceptions[0], TypeConflictError(INTEGER_TYPE, BOOLEAN_TYPE,
                TokenLocation(1, 9, 1, 12)
            ))
        }

        getCompilationResults("""
            let a := 2
            def b : Int := a + 2
        """.trimIndent()). let { (values, exceptions) ->
            {
                assert(exceptions.isEmpty())
                val two = WilesInteger(2)
                val four = WilesInteger(4)
                assertValue(values, "!a") { valueEquals(it, two) }
                assertValue(values, "!a") { typeEquals(it, INTEGER_TYPE.exactly(two)) }
                assertValue(values, "!b") { valueEquals(it, four) }
                assertValue(values, "!b") { typeEquals(it, INTEGER_TYPE) }
            }
        }

        getRunningResults("""
            def a : Int := a + 1
            let b := a
        """.trimIndent()).let { (_, exceptions) ->
            //TODO: check for exactly 1
            assert(exceptions.isNotEmpty())
            assertEquals(exceptions[0], StackOverflowException(
                TokenLocation(1, 18, 1, 19)
            ))
        }

        getCompilationResults("def a := 123").let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], InferenceFailureException(
                TokenLocation(1,5,1,6)
            ))
        }
    }

    @Test
    fun standardLibraryTest()
    {
        getCompilationResults("").let{ (values, exceptions) ->
            assert(exceptions.isEmpty())
            assertValue(values, "!true"){valueEquals(it, true)}
            assertValue(values, "!true"){typeEquals(it, BooleanType())}

            assertValue(values, "!false"){valueEquals(it, false)}
            assertValue(values, "!false"){typeEquals(it, BooleanType())}

            assertValue(values, "!nothing"){valueEquals(it, WilesNothing)}
            assertValue(values, "!nothing"){typeEquals(it, NothingType())}

            assertValue(values, "!Int"){valueEquals(it, INTEGER_TYPE)}
            assertValue(values, "!Int"){typeEquals(it, TypeType())}

            assertValue(values, "!Text"){valueEquals(it, TEXT_TYPE)}
            assertValue(values, "!Text"){typeEquals(it, TypeType())}

            assertValue(values, "!Decimal"){valueEquals(it, DECIMAL_TYPE)}
            assertValue(values, "!Decimal"){typeEquals(it, TypeType())}
        }
    }
}