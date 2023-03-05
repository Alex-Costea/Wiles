
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import wiles.Main
import wiles.interpreter.Interpreter
import wiles.interpreter.data.VariableMap
import wiles.interpreter.statics.InterpreterConstants.objectsMap

class InterpreterTests {
    fun assertVar(vars : VariableMap, name : String, value : Any?)
    {
        assert(vars[name] in objectsMap.keys)
        assert(objectsMap[vars[name]]!!.value == value)
    }

    fun getVars(code : String) : VariableMap
    {
        val interpreter = Interpreter(code)
        interpreter.interpret()
        return interpreter.newVars
    }

    @Test
    fun expressionTests()
    {

        // let result := 10
        val vars1 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT64",
      "type" : "TYPE"
    }, {
      "name" : "!result",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#10",
        "type" : "TOKEN"
      } ]
    } ]
  } ]
}""")
        assertVar(vars1, "!result", 10L)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            Main.DEBUG = true
        }
    }
}