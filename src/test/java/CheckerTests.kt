import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import wiles.checker.Checker
import wiles.checker.exceptions.*
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.STRING_ID
import wiles.shared.constants.Utils.NULL_LOCATION
import kotlin.test.assertEquals

class CheckerTests {

    private fun createExceptions(vararg list: AbstractCompilationException): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        exceptions.addAll(listOf(*list))
        return exceptions
    }

    private fun checkResult(exceptions : CompilationExceptionsCollection?, code : String, result : String)
    {
        val checker = Checker(code)
        val exceptionList = exceptions ?: CompilationExceptionsCollection()
        Assertions.assertEquals(exceptionList, checker.check())
        assertEquals(result,checker.code.toString())
    }
    @Test
    fun inferFromDeclaration()
    {
        checkResult(null, """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#10",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; #10)))")

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@10",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE STRING; !a; EXPRESSION(TYPE STRING; @10)))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION, TYPE + INT64_ID, TYPE + STRING_ID)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT64",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      }
    }, {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@10",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 20
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE STRING; @10)))")

        checkResult(createExceptions(InferenceFailException(NULL_LOCATION)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE NOTHING; !a; EXPRESSION(TYPE NOTHING; NOTHING)))")

        checkResult(createExceptions(VariableAlreadyDeclaredException(NULL_LOCATION)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#1",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; #1)); DECLARATION(!a; EXPRESSION(#2)))")

    checkResult(null,
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "NOTHING",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      }
    }, {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 2,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "!int",
        "type" : "TYPE"
      }, {
        "name" : "NOTHING",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!b",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 17
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE NOTHING; !a); DECLARATION(TYPE EITHER; (TYPE !int; TYPE NOTHING); !b; EXPRESSION(TYPE NOTHING; !a)))")

    checkResult(createExceptions(UsedBeforeInitializationException(NULL_LOCATION)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!int",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      }
    }, {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!b",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE !int; !a); DECLARATION(!b; EXPRESSION(!a)))")

        checkResult(createExceptions(UnknownIdentifierException(NULL_LOCATION)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!b",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(!a; EXPRESSION(!b)))")
    }

    companion object {
        private const val TYPE = "TYPE "
    }
}