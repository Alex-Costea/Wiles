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

    checkResult(null,"""{
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
        "name" : "INT64",
        "type" : "TYPE",
        "location" : {
          "line" : 2,
          "lineIndex" : 9
        }
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
          "lineIndex" : 21
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE NOTHING; !a); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !b; EXPRESSION(TYPE NOTHING; !a)))")

    checkResult(createExceptions(UsedBeforeInitializationException(NULL_LOCATION)),
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
}""", "CODE_BLOCK(DECLARATION(TYPE INT64; !a); DECLARATION(!b; EXPRESSION(!a)))")

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

        checkResult(createExceptions(UnknownTypeException(NULL_LOCATION)),
            """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!fake",
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
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE !fake; !a))")
        }

    @Test
    fun inferFromExpression()
    {
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
        "name" : "UNARY_PLUS",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      }, {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 11
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; NOTHING|UNARY_PLUS|INT64; #2)))")


        checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE EITHER; (TYPE INT64; TYPE NOTHING)","TYPE INT64")),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "INT64",
        "type" : "TYPE"
      }, {
        "name" : "NOTHING",
        "type" : "TYPE"
      } ]
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
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 21
        }
      } ]
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
      }, {
        "name" : "EQUALS",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 12
        }
      }, {
        "name" : "#3",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 14
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !a; EXPRESSION(TYPE INT64; #2)); DECLARATION(!b; EXPRESSION(!a; EQUALS; #3)))")

    checkResult(null,
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
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#3",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE INT64; !a); EXPRESSION(TYPE NOTHING; EXPRESSION(TYPE INT64; !a); INT64|ASSIGN|INT64; EXPRESSION(TYPE INT64; #3)))")


        checkResult(createExceptions(CannotModifyException(NULL_LOCATION)),"""{
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
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#3",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(EXPRESSION(!a); ASSIGN; EXPRESSION(#3)))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 14
        }
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#3",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE INT64; !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(TYPE NOTHING; EXPRESSION(TYPE INT64; !a); INT64|ASSIGN|INT64; EXPRESSION(TYPE INT64; #3)))")

    checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE INT64","TYPE STRING")),
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 14
        }
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@3",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE INT64; !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(EXPRESSION(TYPE INT64; !a); ASSIGN; EXPRESSION(TYPE STRING; @3)))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 13
      },
      "components" : [ {
        "name" : "INT64",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 20
        }
      }, {
        "name" : "STRING",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 28
        }
      } ]
    }, {
      "name" : "!a",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 37
        }
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@3",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE EITHER; (TYPE INT64; TYPE STRING); !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(TYPE NOTHING; EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE STRING); !a); EITHER|ASSIGN|STRING; EXPRESSION(TYPE STRING; @3)))")

    checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE STRING", "TYPE EITHER; (TYPE STRING; TYPE NOTHING)")),
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "STRING",
        "type" : "TYPE"
      }, {
        "name" : "NOTHING",
        "type" : "TYPE"
      } ]
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
        "name" : "@3",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 18
        }
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE",
      "location" : {
        "line" : 2,
        "lineIndex" : 9
      }
    }, {
      "name" : "!b",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!b",
        "type" : "TOKEN",
        "location" : {
          "line" : 3,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 3,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 3,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE STRING; TYPE NOTHING); !a; EXPRESSION(TYPE STRING; @3)); DECLARATION(TYPE STRING; !b); EXPRESSION(EXPRESSION(TYPE STRING; !b); ASSIGN; EXPRESSION(TYPE EITHER; (TYPE STRING; TYPE NOTHING); !a)))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "STRING",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 16
        }
      }, {
        "name" : "INT64",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 22
        }
      } ]
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
        "name" : "@3",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 34
        }
      } ]
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
        "name" : "@hi",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 10
        }
      }, {
        "name" : "PLUS",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 15
        }
      }, {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 17
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE STRING; TYPE INT64); !a; EXPRESSION(TYPE STRING; @3)); DECLARATION(TYPE STRING; !b; EXPRESSION(TYPE STRING; @hi; STRING|PLUS|ANYTHING; !a)))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "DOUBLE",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 16
        }
      }, {
        "name" : "STRING",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 26
        }
      } ]
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
        "name" : "@a",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 35
        }
      } ]
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
        "name" : "INT64",
        "type" : "TYPE",
        "location" : {
          "line" : 2,
          "lineIndex" : 16
        }
      }, {
        "name" : "STRING",
        "type" : "TYPE",
        "location" : {
          "line" : 2,
          "lineIndex" : 25
        }
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
        "name" : "#1",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 34
        }
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "EITHER",
      "type" : "TYPE",
      "location" : {
        "line" : 3,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "DOUBLE",
        "type" : "TYPE",
        "location" : {
          "line" : 3,
          "lineIndex" : 16
        }
      }, {
        "name" : "STRING",
        "type" : "TYPE",
        "location" : {
          "line" : 3,
          "lineIndex" : 26
        }
      } ]
    }, {
      "name" : "!c",
      "type" : "TOKEN",
      "location" : {
        "line" : 3,
        "lineIndex" : 5
      }
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!c",
        "type" : "TOKEN",
        "location" : {
          "line" : 4,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 4,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 4,
          "lineIndex" : 6
        }
      }, {
        "name" : "PLUS",
        "type" : "TOKEN",
        "location" : {
          "line" : 4,
          "lineIndex" : 8
        }
      }, {
        "name" : "!b",
        "type" : "TOKEN",
        "location" : {
          "line" : 4,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !a; EXPRESSION(TYPE STRING; @a)); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE STRING); !b; EXPRESSION(TYPE INT64; #1)); DECLARATION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !c); EXPRESSION(TYPE NOTHING; EXPRESSION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !c); EITHER|ASSIGN|EITHER; EXPRESSION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !a; ANYTHING|PLUS|ANYTHING; !b)))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!temp",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 13
        }
      }, {
        "name" : "PLUS",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 15
        }
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "MUTABLE",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 17
          }
        }, {
          "name" : "#3",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 21
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE INT64; !temp; EXPRESSION(TYPE INT64; #2; INT64|PLUS|INT64; EXPRESSION(TYPE MUTABLE; (TYPE INT64); NOTHING|MUTABLE|INT64; #3))))")
    }

    @Test
    fun inferFromList()
    {
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
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 16
        },
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 11
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 13
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 15
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE INT64); !a; EXPRESSION(TYPE LIST; (TYPE INT64); LIST(TYPE INT64; EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE INT64; #3)))))")

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
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 18
        },
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 11
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 13
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "@3",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 15
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(!a; EXPRESSION(LIST(EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE STRING; @3)))))")

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
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 22
        },
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 26
          },
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 26
            }
          }, {
            "name" : "NOTHING",
            "type" : "TYPE"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 11
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 13
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 15
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE NOTHING)); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE NOTHING)); LIST(TYPE EITHER; (TYPE INT64; TYPE NOTHING); EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE NOTHING; NOTHING)))))")

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
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 38
        },
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 42
          },
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 49
            }
          }, {
            "name" : "EITHER",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 53
            },
            "components" : [ {
              "name" : "EITHER",
              "type" : "TYPE",
              "location" : {
                "line" : 1,
                "lineIndex" : 60
              },
              "components" : [ {
                "name" : "INT64",
                "type" : "TYPE",
                "location" : {
                  "line" : 1,
                  "lineIndex" : 60
                }
              }, {
                "name" : "NOTHING",
                "type" : "TYPE"
              } ]
            }, {
              "name" : "STRING",
              "type" : "TYPE",
              "location" : {
                "line" : 1,
                "lineIndex" : 65
              }
            } ]
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 11
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 13
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "MUTABLE",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 16
            }
          }, {
            "name" : "@nothing",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 20
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 31
            }
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "LIST",
      "type" : "TYPE",
      "location" : {
        "line" : 2,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "EITHER",
        "type" : "TYPE",
        "location" : {
          "line" : 2,
          "lineIndex" : 14
        },
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE",
          "location" : {
            "line" : 2,
            "lineIndex" : 21
          }
        }, {
          "name" : "EITHER",
          "type" : "TYPE",
          "location" : {
            "line" : 2,
            "lineIndex" : 25
          },
          "components" : [ {
            "name" : "STRING",
            "type" : "TYPE",
            "location" : {
              "line" : 2,
              "lineIndex" : 25
            }
          }, {
            "name" : "NOTHING",
            "type" : "TYPE"
          } ]
        } ]
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
          "lineIndex" : 36
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING))); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING))); LIST(TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING)); EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE MUTABLE; (TYPE STRING); NOTHING|MUTABLE|STRING; @nothing); EXPRESSION(TYPE NOTHING; NOTHING)))); DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE STRING; TYPE NOTHING))); !b; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING))); !a)))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE STRING))","TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING)))")),
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
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 38
        },
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 42
          },
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 49
            }
          }, {
            "name" : "EITHER",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 53
            },
            "components" : [ {
              "name" : "EITHER",
              "type" : "TYPE",
              "location" : {
                "line" : 1,
                "lineIndex" : 60
              },
              "components" : [ {
                "name" : "INT64",
                "type" : "TYPE",
                "location" : {
                  "line" : 1,
                  "lineIndex" : 60
                }
              }, {
                "name" : "NOTHING",
                "type" : "TYPE"
              } ]
            }, {
              "name" : "STRING",
              "type" : "TYPE",
              "location" : {
                "line" : 1,
                "lineIndex" : 65
              }
            } ]
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 11
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 13
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "MUTABLE",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 16
            }
          }, {
            "name" : "@nothing",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 20
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 31
            }
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "LIST",
      "type" : "TYPE",
      "location" : {
        "line" : 2,
        "lineIndex" : 9
      },
      "components" : [ {
        "name" : "EITHER",
        "type" : "TYPE",
        "location" : {
          "line" : 2,
          "lineIndex" : 14
        },
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE",
          "location" : {
            "line" : 2,
            "lineIndex" : 21
          }
        }, {
          "name" : "STRING",
          "type" : "TYPE",
          "location" : {
            "line" : 2,
            "lineIndex" : 25
          }
        } ]
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
          "lineIndex" : 35
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING))); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING))); LIST(TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING)); EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE MUTABLE; (TYPE STRING); NOTHING|MUTABLE|STRING; @nothing); EXPRESSION(TYPE NOTHING; NOTHING)))); DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE STRING)); !b; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); TYPE STRING))); !a)))")

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
        "name" : "MUTABLE",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      }, {
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 15
        },
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 19
          }
        } ]
      } ]
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
        "name" : "INT64",
        "type" : "TYPE",
        "location" : {
          "line" : 2,
          "lineIndex" : 9
        }
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
      }, {
        "name" : "ELEM_ACCESS",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 19
        }
      }, {
        "name" : "#0",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 21
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE INT64))); !a; EXPRESSION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE INT64))); NOTHING|MUTABLE|LIST; LIST(TYPE INT64))); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !b; EXPRESSION(TYPE EITHER; (TYPE MUTABLE; (TYPE INT64); TYPE NOTHING); !a; LIST|ELEM_ACCESS|INT64; #0)))")
    }

    @Test
    fun methodCallTest()
    {
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 1
        }
      }, {
        "name" : "ACCESS",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 2
        }
      }, {
        "name" : "!as_text",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 3
        }
      } ]
    }, {
      "name" : "ACCESS",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 10
      }
    }, {
      "name" : "!write",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 11
      }
    } ]
  } ]
}""", "CODE_BLOCK(EXPRESSION(TYPE NOTHING; !TYPE STRING!write; METHOD|APPLY|METHOD_CALL; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE STRING; !TYPE ANYTHING!as_text; METHOD|APPLY|METHOD_CALL; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE INT64; #2))))))))))")
    }

    companion object {
        private const val TYPE = "TYPE "
    }
}