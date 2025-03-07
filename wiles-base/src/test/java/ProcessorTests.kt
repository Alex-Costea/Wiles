
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable
import wiles.parser.Parser
import wiles.processor.Processor
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.errors.*
import wiles.processor.types.*
import wiles.processor.types.AbstractType.Companion.ANYTHING_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.FALSE_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.types.AbstractType.Companion.TRUE_TYPE
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesNothing
import wiles.shared.constants.Utils
import wiles.shared.data.TokenLocation
import wiles.shared.data.WilesExceptionsCollection
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

    private fun objectEquals(myValue : Value, compared : Any?): Boolean {
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
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("let c := 3.0").let { (values, exceptions) ->
            val obj = WilesDecimal("3.0")
            assertValue(values, "!c") { objectEquals(it, obj) }
            assertValue(values, "!c") { typeEquals(it, DECIMAL_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("""let b := "hello!";""").let { (values, exceptions) ->
            val obj = "hello!"
            assertValue(values, "!b") { objectEquals(it, obj) }
            assertValue(values, "!b") { typeEquals(it, TEXT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("let a := abc").let{ (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], IdentifierUnknownException(
                TokenLocation(1, 10, 1, 13)
            ))
        }

        getCompilationResults("""
            let a := 7
            let b := a
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val obj = WilesInteger(7)
            assertValue(values, "!a") {objectEquals(it, obj)}
            assertValue(values, "!a") {typeEquals(it, INT_TYPE.exactly(obj))}
            assertValue(values, "!b") {objectEquals(it, obj)}
            assertValue(values, "!b") {typeEquals(it, INT_TYPE.exactly(obj))}
            assertEquals(values["!a"], values["!b"])
        }

        getCompilationResults("""
            let a := 2
            let a := 3
        """.trimIndent()).let { (values, exceptions) ->
            {
                assertEquals(exceptions.size, 1)
                assertEquals(exceptions[0], IdentifierAlreadyDeclaredException(
                    TokenLocation(1, 4, 1, 5)
                ))
                val value = WilesInteger(2)
                assertValue(values, "!a") {objectEquals(it, value)}
                assertValue(values, "!a") { typeEquals(it, INT_TYPE)}
            }
        }

        getCompilationResults("let const a := rand()").let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], ValueNotConstException(
                TokenLocation(1, 11, 1, 12)
            ))
        }
    }

    @Test
    fun typeDefTests(){
        getCompilationResults("let a : Int := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("let a : Anything := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("let var a : Anything := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, ANYTHING_TYPE) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("let var a := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE) }
            assertEquals(exceptions.size, 0)
        }

        getRunningResults("let var a := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getRunningResults("let var a : Int; a := 3").let { (values, exceptions) ->
            val obj = WilesInteger(3)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("let a : Text := 3").let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], TypeConflictError(TEXT_TYPE,INT_TYPE.exactly(WilesInteger(3)),
                TokenLocation(1, 9, 1, 13)
            ))
        }

        getCompilationResults("let a : 123 := 123").let { (values, exceptions) ->
            val obj = WilesInteger(123)
            assertValue(values, "!a") { objectEquals(it, obj) }
            assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(obj)) }
            assertEquals(exceptions.size, 0)
        }

        getCompilationResults("""
            let a := rand()
            let b : a := 0.5
        """.trimIndent()).let { (_, exceptions) ->
            {
                assertEquals(exceptions.size, 1)
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
            assertEquals(exceptions.size, 0)
            val value = WilesInteger(5)
            assertValue(values, "!a"){objectEquals(it, value)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(value))}
        }
        getCompilationResults("""let a := "hello, " + "world!";""").let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val value = "hello, world!"
            assertValue(values, "!a"){objectEquals(it, value)}
            assertValue(values, "!a"){typeEquals(it, TEXT_TYPE.exactly(value))}
        }
        getCompilationResults("""
            let a := 1 + "a"
            let b := "b" + 2
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a"){objectEquals(it, "1a")}
            assertValue(values, "!a"){typeEquals(it, TEXT_TYPE.exactly("1a"))}
            assertValue(values, "!b"){objectEquals(it, "b2")}
            assertValue(values, "!b"){typeEquals(it, TEXT_TYPE.exactly("b2"))}
        }

        getCompilationResults("""
            let a := 1 + 2.0
            let b := 1.0 + 3
            let c := 3.0 + 2.0
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a"){objectEquals(it, WilesDecimal("3.0"))}
            assertValue(values, "!a"){typeEquals(it, DECIMAL_TYPE.exactly(WilesDecimal("3.0")))}
            assertValue(values, "!b"){objectEquals(it, WilesDecimal("4.0"))}
            assertValue(values, "!b"){typeEquals(it, DECIMAL_TYPE.exactly(WilesDecimal("4.0")))}
            assertValue(values, "!c"){objectEquals(it, WilesDecimal("5.0"))}
            assertValue(values, "!c"){typeEquals(it, DECIMAL_TYPE.exactly(WilesDecimal("5.0")))}
        }

        getCompilationResults("""
            let var a : Number := rand()
            let var b : Number := rand()
            let c := a + b
        """.trimIndent()).let{ (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a"){typeEquals(it, NUMBER_TYPE)}
            assertValue(values, "!b"){typeEquals(it, NUMBER_TYPE)}
            assertValue(values, "!c"){typeEquals(it, DECIMAL_TYPE)}
        }

        getCompilationResults("""
            let a := 5 - 1.0
            let b := - 1.0
            let c := + 1.0
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val val1 = WilesDecimal("4.0")
            val val2 = WilesDecimal("-1.0")
            val val3 = WilesDecimal("1.0")
            assertValue(values, "!a"){objectEquals(it, val1)}
            assertValue(values, "!a"){typeEquals(it, DECIMAL_TYPE.exactly(val1))}
            assertValue(values, "!b"){objectEquals(it, val2)}
            assertValue(values, "!b"){typeEquals(it, DECIMAL_TYPE.exactly(val2))}
            assertValue(values, "!c"){objectEquals(it, val3)}
            assertValue(values, "!c"){typeEquals(it, DECIMAL_TYPE.exactly(val3))}

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
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a") {objectEquals(it, WilesInteger(3))}
            assertValue(values, "!a") {typeEquals(it, INT_TYPE)}
            assertValue(values, "!b") {objectEquals(it, WilesInteger(2))}
            assertValue(values, "!b") {typeEquals(it, INT_TYPE.exactly(WilesInteger(2)))}
        }

        getCompilationResults("""
            let var a := 2
            a := 3
            a := 4
        """.trimIndent()). let{(values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a") {objectEquals(it, WilesInteger(4))}
            assertValue(values, "!a") {typeEquals(it, INT_TYPE)}
        }

        getCompilationResults("a := 123") .let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], IdentifierUnknownException(
                TokenLocation(1, 1, 1, 2)
            ))
        }

        getCompilationResults("""
            let a := 123
            a := 345
        """.trimIndent()). let { (values, exceptions) ->
            assertValue(values, "!a") {objectEquals(it, WilesInteger(123))}
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0],
                CantBeModifiedException(TokenLocation(2, 1, 2, 2)))
        }

        getCompilationResults("""
            17 := 25
        """.trimIndent()). let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0],
                CantBeModifiedException(TokenLocation(1, 1, 1, 3)))
        }

        getCompilationResults("""
            let var a := 1
            a := "text"
        """.trimIndent()).let{ (values, exceptions) ->
            assertValue(values, "!a") { objectEquals(it, WilesInteger(1))}
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], TypeConflictError( INT_TYPE, TEXT_TYPE.exactly("text"),
                TokenLocation(2, 1, 2, 2)
            ))
        }

    }

    @Test
    fun randTest()
    {
        getRunningResults("let a := rand()").let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a") {it.getObj() is WilesDecimal}
            assertValue(values, "!a") {(it.getObj() as WilesDecimal).toString().startsWith("0.")}
            assertValue(values, "!a") {(it.getType() is DecimalType) && it.getType().getValue() == it.getObj()}
        }

        getCompilationResults("let a := rand() + 4").let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a") {objectEquals(it, null)}
            assertValue(values, "!a") {typeEquals(it, DECIMAL_TYPE)}
        }

        getRunningResults("let a := rand() + 4").let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a") {it.getObj() is WilesDecimal}
            assertValue(values, "!a") {(it.getObj() as WilesDecimal).toString()[0] == '4'}
            assertValue(values, "!a") {(it.getType() is DecimalType) && it.getType().getValue() == it.getObj()}
        }

        getCompilationResults("""
            let var a := rand()
            let b := a
            a := 1.2
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!a") {objectEquals(it, WilesDecimal("1.2"))}
            assertValue(values, "!a") {it.getType() == DECIMAL_TYPE}
            assertValue(values, "!b") {it.getObj() == null}
            assertValue(values, "!b") {it.getType() == DECIMAL_TYPE}
        }

        getRunningResults("""
            let var a := rand()
            let b := a
            a := 1.2
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val obj1 = WilesDecimal("1.2")
            assertValue(values, "!a") {objectEquals(it, WilesDecimal("1.2"))}
            assertValue(values, "!a") {it.getType() == DECIMAL_TYPE.exactly(obj1)}
            assertValue(values, "!b") {it.getObj() is WilesDecimal}
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
            assertEquals(exceptions.size, 0)
            val value123 = WilesInteger(123)
            val value128 = WilesInteger(128)
            assertValue(values, "!a"){objectEquals(it, value123)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(value123))}
            assertValue(values, "!b"){objectEquals(it, value128)}
            assertValue(values, "!b"){typeEquals(it, INT_TYPE.exactly(value128))}
        }

        getCompilationResults("def a : Int := true"). let{ (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], TypeConflictError(INT_TYPE, TRUE_TYPE,
                TokenLocation(1, 9, 1, 12)
            ))
        }

        getCompilationResults("""
            let a := 2
            def b : Int := a + 2
        """.trimIndent()). let { (values, exceptions) ->
            {
                assertEquals(exceptions.size, 0)
                val two = WilesInteger(2)
                val four = WilesInteger(4)
                assertValue(values, "!a") { objectEquals(it, two) }
                assertValue(values, "!a") { typeEquals(it, INT_TYPE.exactly(two)) }
                assertValue(values, "!b") { objectEquals(it, four) }
                assertValue(values, "!b") { typeEquals(it, INT_TYPE) }
            }
        }

        getCompilationResults("def a := 123").let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], InferenceFailureException(
                TokenLocation(1,5,1,6)
            ))
        }
    }

    @Test
    fun stackOverflowTest()
    {
        getRunningResults("""
            def a : Int := a + 1
            let b := a
        """.trimIndent()).let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], StackOverflowException(
                TokenLocation(1, 18, 1, 19)
            ))
        }

    }

    @Test
    fun initializationTest()
    {
        getCompilationResults("""
            let a : Int
            a := 10
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val obj = WilesInteger(10)
            assertValue(values, "!a"){objectEquals(it, obj)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(obj))}
        }

        getCompilationResults("""
            let a : Int
            let b := a
        """.trimIndent()).let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], ValueUndefinedException(
                TokenLocation(2, 10, 2, 11)
            ))
        }

        getCompilationResults("""
            let a : Int
            a := 10
            a := 20
        """.trimIndent()).let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], CantBeModifiedException(
                TokenLocation(3, 1, 3, 2)
            ))
        }
    }

    @Test
    fun eitherTest()
    {
        getCompilationResults("let a : 1 | 2 := 1").let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(1)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(myObj))}
        }

        getCompilationResults("let var a : 1 | 2 := 1").let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(1)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it,
                EitherType(INT_TYPE.exactly(WilesInteger(1)), INT_TYPE.exactly(WilesInteger(2))))}
        }

        getCompilationResults("let var a : 1 | 2 := 1; a := 2").let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(2)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it,
                EitherType(INT_TYPE.exactly(WilesInteger(1)), INT_TYPE.exactly(WilesInteger(2))))}
        }

        getRunningResults("let var a : 1 | 2 := 1; a := 2").let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(2)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(myObj))}
        }

        getCompilationResults("let var a : 1 | 2; a := 2").let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(2)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it,
                EitherType(INT_TYPE.exactly(WilesInteger(1)), INT_TYPE.exactly(WilesInteger(2))))}
        }

        getRunningResults("let var a : 1 | 2; a := 2").let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(2)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(myObj))}
        }
        getCompilationResults("""
            let const MyType := 1 | 2
            let a : MyType := 2
        """.trimIndent()).let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)

            val myType = EitherType(INT_TYPE.exactly(WilesInteger(1)), INT_TYPE.exactly(WilesInteger(2)))
            assertValue(values, "!MyType"){objectEquals(it, myType)}

            val myObj = WilesInteger(2)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(myObj))}
        }

        getRunningResults("""
            let const MyType := 1 | 2
            let a : MyType := 2
        """.trimIndent()).let {  (values, exceptions) ->
            assertEquals(exceptions.size, 0)

            val myType = EitherType(INT_TYPE.exactly(WilesInteger(1)), INT_TYPE.exactly(WilesInteger(2)))
            assertValue(values, "!MyType"){objectEquals(it, myType)}

            val myObj = WilesInteger(2)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, INT_TYPE.exactly(myObj))}
        }

        getCompilationResults("""
            let var a : 1 | 2 := 1
            let b : Int := a
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(1)
            val myType = EitherType(INT_TYPE.exactly(WilesInteger(1)), INT_TYPE.exactly(WilesInteger(2)))
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, myType)}
            assertValue(values, "!b"){objectEquals(it, myObj)}
            assertValue(values, "!b"){typeEquals(it, INT_TYPE.exactly(myObj))}
        }

        getRunningResults("""
            let var a : 1 | 2 := 1
            let b : Int := a
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val myObj = WilesInteger(1)
            val myType = INT_TYPE.exactly(myObj)
            assertValue(values, "!a"){objectEquals(it, myObj)}
            assertValue(values, "!a"){typeEquals(it, myType)}
            assertValue(values, "!b"){objectEquals(it, myObj)}
            assertValue(values, "!b"){typeEquals(it, myType)}
        }

        getCompilationResults("""
            let var a : Decimal | Text := rand()
            let b : Int | Text := a
        """.trimIndent()).let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            val type1 = EitherType(DECIMAL_TYPE, TEXT_TYPE)
            val type2 = EitherType(INT_TYPE, TEXT_TYPE)
            assertEquals(exceptions[0], TypeConflictError(type2, type1,
                TokenLocation(2, 13, 2, 14)))
        }

        getCompilationResults("""
            let var a : Decimal | Text := rand()
            let b : Int | Text | Decimal := a
        """.trimIndent()).let { (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            val type = EitherType(DECIMAL_TYPE, TEXT_TYPE)
            assertValue(values, "!a"){objectEquals(it, null)}
            assertValue(values, "!a"){typeEquals(it, type)}
            assertValue(values, "!b"){objectEquals(it, null)}
            assertValue(values, "!b"){typeEquals(it, type)}
        }
    }

    @Test
    fun standardLibraryTest()
    {
        getCompilationResults("").let{ (values, exceptions) ->
            assertEquals(exceptions.size, 0)
            assertValue(values, "!true"){objectEquals(it, true)}
            assertValue(values, "!true"){typeEquals(it, TRUE_TYPE)}

            assertValue(values, "!false"){objectEquals(it, false)}
            assertValue(values, "!false"){typeEquals(it, FALSE_TYPE)}

            assertValue(values, "!nothing"){objectEquals(it, WilesNothing)}
            assertValue(values, "!nothing"){typeEquals(it, NothingType())}

            assertValue(values, "!Int"){objectEquals(it, INT_TYPE)}
            assertValue(values, "!Int"){typeEquals(it, TypeType())}

            assertValue(values, "!Text"){objectEquals(it, TEXT_TYPE)}
            assertValue(values, "!Text"){typeEquals(it, TypeType())}

            assertValue(values, "!Decimal"){objectEquals(it, DECIMAL_TYPE)}
            assertValue(values, "!Decimal"){typeEquals(it, TypeType())}

            assertValue(values, "!Anything"){objectEquals(it, ANYTHING_TYPE)}
            assertValue(values, "!Anything"){typeEquals(it, TypeType())}
        }
    }

    @Test
    fun unusedValueTest()
    {
        getCompilationResults("1+2").let { (_, exceptions) ->
            assertEquals(exceptions.size, 1)
            assertEquals(exceptions[0], ValueUnusedException(
                TokenLocation(1,2,1,3)
            ))
        }
    }

}