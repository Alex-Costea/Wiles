import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import wiles.Main
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
        // let func : fun[] := fun(x := 10, arg y := 10) do nothing
        checkResult(null, """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "METHOD",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 12
      },
      "components" : [ {
        "type" : "METHOD"
      } ]
    }, {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "location" : {
          "line" : 1,
          "lineIndex" : 21
        },
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!x",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 25
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#10",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 30
              }
            } ]
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!y",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 38
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#10",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 43
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 50
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !x; EXPRESSION(TYPE INT64; #10)); DECLARATION ANON_ARG; (TYPE INT64; !y; EXPRESSION(TYPE INT64; #10)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !x; EXPRESSION(TYPE INT64; #10)); DECLARATION ANON_ARG; (TYPE INT64; !y; EXPRESSION(TYPE INT64; #10)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

        // let func : fun[] := fun(x : int) do nothing
        checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE METHOD; (METHOD(TYPE !nothing))","TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !x)))")),
            """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "METHOD",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 12
      },
      "components" : [ {
        "type" : "METHOD"
      } ]
    }, {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "location" : {
          "line" : 1,
          "lineIndex" : 21
        },
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 29
            }
          }, {
            "name" : "!x",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 25
            }
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 37
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !x))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !x); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

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
        "name" : "!nothing",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE !nothing; !a; EXPRESSION(TYPE !nothing; !nothing)))")

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
      "name" : "!nothing",
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
        "name" : "!nothing",
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
}""","CODE_BLOCK(DECLARATION(TYPE !nothing; !a; EXPRESSION(!nothing)); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE !nothing); !b; EXPRESSION(TYPE !nothing; !a)))")

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
        checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION)),"""{
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
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 9
        }
      }, {
        "name" : "!nothing",
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
          "lineIndex" : 17
        }
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 10
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!a",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 11
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT64; TYPE !nothing); !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(!writeline; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE !nothing); !a))))))")

        checkResult(createExceptions(InvalidLiteralException(NULL_LOCATION)),"""{
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
        "name" : "#999999999999999999999999999999",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(!a; EXPRESSION(#999999999999999999999999999999)))")

        checkResult(createExceptions(ExpectedIdentifierException(NULL_LOCATION)),"""{
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
        "name" : "IMPORT",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      }, {
        "name" : "#1",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 17
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(!a; EXPRESSION(IMPORT; #1)))")

        checkResult(createExceptions(UnusedExpressionException(NULL_LOCATION)),"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "#1",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 1
      }
    } ]
  } ]
}""","CODE_BLOCK(EXPRESSION(TYPE INT64; #1))")

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
}""","CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; !nothing|UNARY_PLUS|INT64; #2)))")


        checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE EITHER; (TYPE INT64; TYPE !nothing)","TYPE INT64")),
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
        "name" : "!nothing",
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
}""", "CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT64; TYPE !nothing); !a; EXPRESSION(TYPE INT64; #2)); DECLARATION(!b; EXPRESSION(!a; EQUALS; #3)))")

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
}""", "CODE_BLOCK(DECLARATION(TYPE INT64; !a); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT64; !a); ASSIGN; EXPRESSION(TYPE INT64; #3)))")


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
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE INT64; !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT64; !a); ASSIGN; EXPRESSION(TYPE INT64; #3)))")

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
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE EITHER; (TYPE INT64; TYPE STRING); !a; EXPRESSION(TYPE INT64; #2)); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE STRING); !a); ASSIGN; EXPRESSION(TYPE STRING; @3)))")

    checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE STRING", "TYPE EITHER; (TYPE STRING; TYPE !nothing)")),
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
        "name" : "!nothing",
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
}""", "CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE STRING; TYPE !nothing); !a; EXPRESSION(TYPE STRING; @3)); DECLARATION(TYPE STRING; !b); EXPRESSION(EXPRESSION(TYPE STRING; !b); ASSIGN; EXPRESSION(TYPE EITHER; (TYPE STRING; TYPE !nothing); !a)))")

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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !a; EXPRESSION(TYPE STRING; @a)); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE STRING); !b; EXPRESSION(TYPE INT64; #1)); DECLARATION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !c); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !c); ASSIGN; EXPRESSION(TYPE EITHER; (TYPE DOUBLE; TYPE STRING); !a; ANYTHING|PLUS|ANYTHING; !b)))")

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
}""","CODE_BLOCK(DECLARATION(TYPE INT64; !temp; EXPRESSION(TYPE INT64; #2; INT64|PLUS|INT64; EXPRESSION(TYPE MUTABLE; (TYPE INT64); MUTABLE; #3))))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 9
      },
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
            "lineIndex" : 24
          }
        } ]
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
        "name" : "MUTABLE",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 34
        }
      }, {
        "name" : "@5",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 38
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
      "name" : "MODIFY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 3
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#10",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE STRING)); !a; EXPRESSION(TYPE MUTABLE; (TYPE STRING); MUTABLE; @5)); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE STRING)); !a); MODIFY; EXPRESSION(TYPE INT64; #10)))")
    }

    @Test
    fun inferFromList()
    {
        // let a := [4, nothing] : anything
        checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE ANYTHING","TYPE !nothing")),"""{
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
          "lineIndex" : 21
        },
        "components" : [ {
          "name" : "ANYTHING",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 25
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#4",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 11
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!nothing",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 14
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(!a; EXPRESSION(LIST(TYPE ANYTHING; EXPRESSION(TYPE INT64; #4); EXPRESSION(TYPE !nothing; !nothing)))))")

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
            "name" : "!nothing",
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
            "name" : "!nothing",
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
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE !nothing)); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE !nothing)); LIST(TYPE EITHER; (TYPE INT64; TYPE !nothing); EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE !nothing; !nothing)))))")

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
                "name" : "!nothing",
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
            "name" : "!nothing",
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
            "name" : "!nothing",
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
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING))); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING))); LIST(TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING)); EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE MUTABLE; (TYPE STRING); MUTABLE; @nothing); EXPRESSION(TYPE !nothing; !nothing)))); DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE STRING; TYPE !nothing))); !b; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING))); !a)))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE STRING))","TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING)))")),
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
                "name" : "!nothing",
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
            "name" : "!nothing",
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
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING))); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING))); LIST(TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING)); EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE MUTABLE; (TYPE STRING); MUTABLE; @nothing); EXPRESSION(TYPE !nothing; !nothing)))); DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE STRING)); !b; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT64; TYPE EITHER; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE STRING))); !a)))")

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
        "name" : "!nothing",
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
}""","CODE_BLOCK(DECLARATION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE INT64))); !a; EXPRESSION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE INT64))); MUTABLE; LIST(TYPE INT64))); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE !nothing); !b; EXPRESSION(TYPE EITHER; (TYPE MUTABLE; (TYPE INT64); TYPE !nothing); !a; LIST|ELEM_ACCESS|INT64; #0)))")
    }

    @Test
    fun methodCallTest()
    {
        //writeline([1,2,3].fun(arg list: list[int]) do yield 10)
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 10
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "LIST",
          "location" : {
            "line" : 1,
            "lineIndex" : 17
          },
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#1",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 12
              }
            } ]
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
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#3",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 16
              }
            } ]
          } ]
        }, {
          "name" : "ACCESS",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 18
          }
        }, {
          "type" : "METHOD",
          "location" : {
            "line" : 1,
            "lineIndex" : 19
          },
          "components" : [ {
            "name" : "ANON_ARG",
            "type" : "DECLARATION",
            "components" : [ {
              "name" : "LIST",
              "type" : "TYPE",
              "location" : {
                "line" : 1,
                "lineIndex" : 33
              },
              "components" : [ {
                "name" : "INT64",
                "type" : "TYPE",
                "location" : {
                  "line" : 1,
                  "lineIndex" : 38
                }
              } ]
            }, {
              "name" : "!list",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 27
              }
            } ]
          }, {
            "type" : "CODE_BLOCK",
            "components" : [ {
              "type" : "RETURN",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "#10",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 1,
                    "lineIndex" : 53
                  }
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(EXPRESSION(TYPE !nothing; !writeline; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE INT64; METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE LIST; (TYPE INT64); !list); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT64; #10)))); METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!list; ASSIGN; EXPRESSION(TYPE LIST; (TYPE INT64); LIST(TYPE INT64; EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE INT64; #3))))))))))")


        //writeline([1,2,3].size)
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 10
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "LIST",
          "location" : {
            "line" : 1,
            "lineIndex" : 17
          },
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#1",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 12
              }
            } ]
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
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#3",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 16
              }
            } ]
          } ]
        }, {
          "name" : "ACCESS",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 18
          }
        }, {
          "name" : "!size",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 19
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(EXPRESSION(TYPE !nothing; !writeline; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE INT64; !TYPE LIST; (TYPE EITHER; (TYPE ANYTHING; TYPE !nothing))!size; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE LIST; (TYPE INT64); LIST(TYPE INT64; EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE INT64; #3))))))))))")

        //let a := 10.modulo(3)
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
        "name" : "#10",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 10
        }
      }, {
        "name" : "ACCESS",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 12
        }
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!modulo",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 13
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 19
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#3",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 20
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; !modulo; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!x; ASSIGN; EXPRESSION(TYPE INT64; #10)); EXPRESSION(!y; ASSIGN; EXPRESSION(TYPE INT64; #3))))))")

        //2.as_text.write
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
}""", "CODE_BLOCK(EXPRESSION(TYPE !nothing; !write; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE STRING; !as_text; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT64; #2))))))))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 21
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 17
            }
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 26
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 31
              }
            } ]
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 42
            }
          }, {
            "name" : "!c",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 38
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!d",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 51
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#4",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 56
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 62
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#40",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 6
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
              "lineIndex" : 10
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 12
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#10",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 15
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(TYPE !nothing; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT64; #10)); EXPRESSION(!c; ASSIGN; EXPRESSION(TYPE INT64; #40)))))")

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 21
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 17
            }
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 26
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 31
              }
            } ]
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 42
            }
          }, {
            "name" : "!c",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 38
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!d",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 51
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#4",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 56
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 62
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!d",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 6
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 8
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#40",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 11
            }
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!c",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 15
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 17
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#30",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 20
            }
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 24
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 26
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#20",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 29
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
              "lineIndex" : 33
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 35
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#10",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 38
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(TYPE !nothing; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT64; #10)); EXPRESSION(!b; ASSIGN; EXPRESSION(TYPE INT64; #20)); EXPRESSION(!c; ASSIGN; EXPRESSION(TYPE INT64; #30)); EXPRESSION(!d; ASSIGN; EXPRESSION(TYPE INT64; #40)))))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 21
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 17
            }
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 26
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 31
              }
            } ]
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 42
            }
          }, {
            "name" : "!c",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 38
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!d",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 51
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#4",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 56
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 62
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 6
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 8
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#10",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 11
            }
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#30",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 15
          }
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#40",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 19
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(TYPE !nothing; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT64; #10)); EXPRESSION(!c; ASSIGN; EXPRESSION(TYPE INT64; #30)); EXPRESSION(!d; ASSIGN; EXPRESSION(TYPE INT64; #40)))))")

    checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION)),
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 21
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 17
            }
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 26
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 31
              }
            } ]
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 42
            }
          }, {
            "name" : "!c",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 38
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!d",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 51
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#4",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 56
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 62
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#30",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 6
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
              "line" : 2,
              "lineIndex" : 10
            }
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 12
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#20",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 15
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(!func; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE INT64; #30); EXPRESSION(!b; ASSIGN; EXPRESSION(TYPE INT64; #20)))))))")

        checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION)),
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 21
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 17
            }
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 26
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 31
              }
            } ]
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 42
            }
          }, {
            "name" : "!c",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 38
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!d",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 51
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#4",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 56
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 62
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#10",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 6
          }
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#20",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 10
          }
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#30",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 14
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #2)); DECLARATION ANON_ARG; (TYPE INT64; !c); DECLARATION ANON_ARG; (TYPE INT64; !d; EXPRESSION(TYPE INT64; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(!func; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE INT64; #10); EXPRESSION(TYPE INT64; #20); EXPRESSION(TYPE INT64; #30))))))")
    }

    @Test
    fun methodsTest()
    {
        /*
        let func := begin
            if true do yield 1
            default do yield 2
        end
         */
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "location" : {
          "line" : 1,
          "lineIndex" : 13
        },
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "IF",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!true",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 10
                }
              } ]
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "RETURN",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#1",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 2,
                      "lineIndex" : 24
                    }
                  } ]
                } ]
              } ]
            }, {
              "name" : "ELSE",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 5
              }
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "RETURN",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#2",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 3,
                      "lineIndex" : 22
                    }
                  } ]
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE INT64)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT64)); METHOD(TYPE INT64; CODE_BLOCK(IF(EXPRESSION(TYPE BOOLEAN; !true); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT64; #1))); ELSE; CODE_BLOCK(RETURN(EXPRESSION(TYPE INT64; #2)))))))))")

        /*
        let func := do
            if begin
                true do yield 1
                default do nothing
            end
         */
        checkResult(createExceptions(ReturnNotGuaranteedException(NULL_LOCATION)),"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "location" : {
          "line" : 1,
          "lineIndex" : 13
        },
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "IF",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!true",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 10
                }
              } ]
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "RETURN",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#1",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 2,
                      "lineIndex" : 24
                    }
                  } ]
                } ]
              } ]
            }, {
              "name" : "ELSE",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 5
              }
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "!nothing",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 3,
                    "lineIndex" : 16
                  }
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(!func; EXPRESSION(METHOD(TYPE INT64; CODE_BLOCK(IF(EXPRESSION(TYPE BOOLEAN; !true); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT64; #1))); ELSE; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))))")

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 22
          }
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#10",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 11
                }
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE INT64)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT64)); METHOD(TYPE INT64; CODE_BLOCK(RETURN(EXPRESSION(TYPE INT64; #10)))))))")

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 22
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing)); METHOD(TYPE !nothing; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "METHOD",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 12
      },
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 20
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 16
            }
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 25
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#30",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 30
              }
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 41
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "MUTABLE",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 46
              }
            }, {
              "name" : "#30",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 50
              }
            } ]
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 54
            }
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#20",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 59
              }
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 66
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT64; !a); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; #30)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE MUTABLE; (TYPE INT64); !b; EXPRESSION(TYPE MUTABLE; (TYPE INT64); MUTABLE; #30)); DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; #20)))); METHOD(TYPE !nothing; DECLARATION(TYPE MUTABLE; (TYPE INT64); !b; EXPRESSION(TYPE MUTABLE; (TYPE INT64); MUTABLE; #30)); DECLARATION(TYPE INT64; !a; EXPRESSION(TYPE INT64; #20)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "MUTABLE",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 11
                }
              }, {
                "name" : "#12",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 15
                }
              } ]
            } ]
          }, {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#3",
                "type" : "TOKEN",
                "location" : {
                  "line" : 3,
                  "lineIndex" : 11
                }
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!b",
      "type" : "TOKEN",
      "location" : {
        "line" : 5,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!func",
        "type" : "TOKEN",
        "location" : {
          "line" : 5,
          "lineIndex" : 10
        }
      }, {
        "name" : "APPLY",
        "type" : "TOKEN",
        "location" : {
          "line" : 5,
          "lineIndex" : 14
        }
      }, {
        "type" : "METHOD_CALL"
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE INT64)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT64)); METHOD(TYPE INT64; CODE_BLOCK(RETURN(EXPRESSION(TYPE MUTABLE; (TYPE INT64); MUTABLE; #12)); RETURN(EXPRESSION(TYPE INT64; #3)))))); DECLARATION(TYPE INT64; !b; EXPRESSION(TYPE INT64; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL)))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT64; !a); DECLARATION ANON_ARG; (TYPE INT64; !b)))","TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT64; !b); DECLARATION ANON_ARG; (TYPE INT64; !a)))")),
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "METHOD",
      "type" : "TYPE",
      "location" : {
        "line" : 1,
        "lineIndex" : 12
      },
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 22
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 20
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 33
            }
          }, {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 31
            }
          } ]
        } ]
      } ]
    }, {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 53
            }
          }, {
            "name" : "!b",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 49
            }
          } ]
        }, {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 66
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 62
            }
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!nothing",
              "type" : "TOKEN",
              "location" : {
                "line" : 1,
                "lineIndex" : 74
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT64; !a); DECLARATION ANON_ARG; (TYPE INT64; !b))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT64; !b); DECLARATION ANON_ARG; (TYPE INT64; !a))); METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT64; !b); DECLARATION ANON_ARG; (TYPE INT64; !a); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

    checkResult(createExceptions(InferenceFailException(NULL_LOCATION)),
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "@10",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 11
                }
              } ]
            } ]
          }, {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#10",
                "type" : "TOKEN",
                "location" : {
                  "line" : 3,
                  "lineIndex" : 11
                }
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(!func; EXPRESSION(METHOD(CODE_BLOCK(RETURN(EXPRESSION(TYPE STRING; @10)); RETURN(EXPRESSION(TYPE INT64; #10)))))))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE INT64","TYPE STRING")),"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 22
          }
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "@10",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 11
                }
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(!func; EXPRESSION(METHOD(TYPE INT64; CODE_BLOCK(RETURN(EXPRESSION(TYPE STRING; @10)))))))")
    }

    @Test
    fun closureTest()
    {
        /*
        let create_sum := fun(arg a : int) do
            yield fun(arg b : int) do
                yield (import a) + b

        let add_5 := create_sum(5)
        let number := add_5(5)
         */

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!create_sum",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "ANON_ARG",
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 31
            }
          }, {
            "name" : "!a",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 27
            }
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "type" : "METHOD",
                "components" : [ {
                  "name" : "ANON_ARG",
                  "type" : "DECLARATION",
                  "components" : [ {
                    "name" : "INT64",
                    "type" : "TYPE",
                    "location" : {
                      "line" : 2,
                      "lineIndex" : 23
                    }
                  }, {
                    "name" : "!b",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 2,
                      "lineIndex" : 19
                    }
                  } ]
                }, {
                  "type" : "CODE_BLOCK",
                  "components" : [ {
                    "type" : "RETURN",
                    "components" : [ {
                      "type" : "EXPRESSION",
                      "components" : [ {
                        "type" : "EXPRESSION",
                        "components" : [ {
                          "name" : "IMPORT",
                          "type" : "TOKEN",
                          "location" : {
                            "line" : 3,
                            "lineIndex" : 16
                          }
                        }, {
                          "name" : "!a",
                          "type" : "TOKEN",
                          "location" : {
                            "line" : 3,
                            "lineIndex" : 23
                          }
                        } ]
                      }, {
                        "name" : "PLUS",
                        "type" : "TOKEN",
                        "location" : {
                          "line" : 3,
                          "lineIndex" : 26
                        }
                      }, {
                        "name" : "!b",
                        "type" : "TOKEN",
                        "location" : {
                          "line" : 3,
                          "lineIndex" : 28
                        }
                      } ]
                    } ]
                  } ]
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!add_5",
      "type" : "TOKEN",
      "location" : {
        "line" : 5,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!create_sum",
        "type" : "TOKEN",
        "location" : {
          "line" : 5,
          "lineIndex" : 14
        }
      }, {
        "name" : "APPLY",
        "type" : "TOKEN",
        "location" : {
          "line" : 5,
          "lineIndex" : 24
        }
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#5",
            "type" : "TOKEN",
            "location" : {
              "line" : 5,
              "lineIndex" : 25
            }
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!number",
      "type" : "TOKEN",
      "location" : {
        "line" : 6,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!add_5",
        "type" : "TOKEN",
        "location" : {
          "line" : 6,
          "lineIndex" : 15
        }
      }, {
        "name" : "APPLY",
        "type" : "TOKEN",
        "location" : {
          "line" : 6,
          "lineIndex" : 20
        }
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#5",
            "type" : "TOKEN",
            "location" : {
              "line" : 6,
              "lineIndex" : 21
            }
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE METHOD; (METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b))); DECLARATION ANON_ARG; (TYPE INT64; !a))); !create_sum; EXPRESSION(TYPE METHOD; (METHOD(TYPE METHOD; (METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b))); DECLARATION ANON_ARG; (TYPE INT64; !a))); METHOD(TYPE METHOD; (METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b))); DECLARATION ANON_ARG; (TYPE INT64; !a); CODE_BLOCK(RETURN(EXPRESSION(TYPE METHOD; (METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b))); METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT64; EXPRESSION(TYPE INT64; IMPORT; !a); INT64|PLUS|INT64; !b)))))))))); DECLARATION(TYPE METHOD; (METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b))); !add_5; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT64; DECLARATION ANON_ARG; (TYPE INT64; !b))); !create_sum; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT64; #5))))); DECLARATION(TYPE INT64; !number; EXPRESSION(TYPE INT64; !add_5; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!b; ASSIGN; EXPRESSION(TYPE INT64; #5))))))")
    }

    @Test
    fun forTests()
    {
        /*
        let list : either[list[int],list[text]] := [1,2,3]
        for i in list from 0 to 2 do
            writeline(i.as_text)
         */
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
        "lineIndex" : 12
      },
      "components" : [ {
        "name" : "LIST",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 19
        },
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 24
          }
        } ]
      }, {
        "name" : "LIST",
        "type" : "TYPE",
        "location" : {
          "line" : 1,
          "lineIndex" : 29
        },
        "components" : [ {
          "name" : "STRING",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 34
          }
        } ]
      } ]
    }, {
      "name" : "!list",
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
          "lineIndex" : 50
        },
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 45
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 47
            }
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN",
            "location" : {
              "line" : 1,
              "lineIndex" : 49
            }
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "FOR",
    "components" : [ {
      "name" : "!i",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "name" : "IN",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 7
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!list",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 10
        }
      } ]
    }, {
      "name" : "FROM",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 15
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#0",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 20
        }
      } ]
    }, {
      "name" : "TO",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 22
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 25
        }
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!writeline",
          "type" : "TOKEN",
          "location" : {
            "line" : 3,
            "lineIndex" : 5
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 3,
            "lineIndex" : 14
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!i",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 15
              }
            }, {
              "name" : "ACCESS",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 16
              }
            }, {
              "name" : "!as_text",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 17
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE LIST; (TYPE INT64); TYPE LIST; (TYPE STRING)); !list; EXPRESSION(TYPE LIST; (TYPE INT64); LIST(TYPE INT64; EXPRESSION(TYPE INT64; #1); EXPRESSION(TYPE INT64; #2); EXPRESSION(TYPE INT64; #3)))); FOR(TYPE EITHER; (TYPE INT64; TYPE STRING); !i; IN; EXPRESSION(TYPE EITHER; (TYPE LIST; (TYPE INT64); TYPE LIST; (TYPE STRING)); !list); FROM; EXPRESSION(TYPE INT64; #0); TO; EXPRESSION(TYPE INT64; #2); CODE_BLOCK(EXPRESSION(TYPE !nothing; !writeline; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE STRING; !as_text; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE STRING); !i))))))))))")
    }

    @Test
    fun whileTests()
    {
        /*
        while true do
            let text := "hi!"
        writeline(text)
         */
        checkResult(createExceptions(UnknownIdentifierException(NULL_LOCATION)),"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "WHILE",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!true",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 7
        }
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "DECLARATION",
        "components" : [ {
          "name" : "!text",
          "type" : "TOKEN",
          "location" : {
            "line" : 2,
            "lineIndex" : 9
          }
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "@hi!",
            "type" : "TOKEN",
            "location" : {
              "line" : 2,
              "lineIndex" : 17
            }
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
      "type" : "TOKEN",
      "location" : {
        "line" : 3,
        "lineIndex" : 1
      }
    }, {
      "name" : "APPLY",
      "type" : "TOKEN",
      "location" : {
        "line" : 3,
        "lineIndex" : 10
      }
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!text",
          "type" : "TOKEN",
          "location" : {
            "line" : 3,
            "lineIndex" : 11
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(WHILE(EXPRESSION(TYPE BOOLEAN; !true); CODE_BLOCK(DECLARATION(TYPE STRING; !text; EXPRESSION(TYPE STRING; @hi!)))); EXPRESSION(!writeline; APPLY; METHOD_CALL(EXPRESSION(!text))))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE BOOLEAN","TYPE INT64")),"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "WHILE",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#1",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 7
        }
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!nothing",
          "type" : "TOKEN",
          "location" : {
            "line" : 1,
            "lineIndex" : 12
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(WHILE(EXPRESSION(TYPE INT64; #1); CODE_BLOCK(EXPRESSION(!nothing))))")
    }

    @Test
    fun ifTests()
    {
        /*
        let func := begin
            let a : int
            if begin
                1 > 2 do a := 1
                default do yield nothing
            end
            ignore(a)
            yield nothing
        end
         */
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "location" : {
          "line" : 1,
          "lineIndex" : 13
        },
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "DECLARATION",
            "components" : [ {
              "name" : "INT64",
              "type" : "TYPE",
              "location" : {
                "line" : 2,
                "lineIndex" : 13
              }
            }, {
              "name" : "!a",
              "type" : "TOKEN",
              "location" : {
                "line" : 2,
                "lineIndex" : 9
              }
            } ]
          }, {
            "type" : "IF",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#1",
                "type" : "TOKEN",
                "location" : {
                  "line" : 3,
                  "lineIndex" : 10
                }
              }, {
                "name" : "LARGER",
                "type" : "TOKEN",
                "location" : {
                  "line" : 3,
                  "lineIndex" : 12
                }
              }, {
                "name" : "#2",
                "type" : "TOKEN",
                "location" : {
                  "line" : 3,
                  "lineIndex" : 14
                }
              } ]
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "!a",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 3,
                      "lineIndex" : 19
                    }
                  } ]
                }, {
                  "name" : "ASSIGN",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 3,
                    "lineIndex" : 21
                  }
                }, {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#1",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 3,
                      "lineIndex" : 24
                    }
                  } ]
                } ]
              } ]
            }, {
              "name" : "ELSE",
              "type" : "TOKEN",
              "location" : {
                "line" : 4,
                "lineIndex" : 5
              }
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "RETURN",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "!nothing",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 4,
                      "lineIndex" : 22
                    }
                  } ]
                } ]
              } ]
            } ]
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!ignore",
              "type" : "TOKEN",
              "location" : {
                "line" : 5,
                "lineIndex" : 5
              }
            }, {
              "name" : "APPLY",
              "type" : "TOKEN",
              "location" : {
                "line" : 5,
                "lineIndex" : 11
              }
            }, {
              "type" : "METHOD_CALL",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "!a",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 5,
                    "lineIndex" : 12
                  }
                } ]
              } ]
            } ]
          }, {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!nothing",
                "type" : "TOKEN",
                "location" : {
                  "line" : 6,
                  "lineIndex" : 11
                }
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""", "CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing)); METHOD(TYPE !nothing; CODE_BLOCK(DECLARATION(TYPE INT64; !a); IF(EXPRESSION(TYPE BOOLEAN; #1; INT64|LARGER|INT64; #2); CODE_BLOCK(EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT64; !a); ASSIGN; EXPRESSION(TYPE INT64; #1))); ELSE; CODE_BLOCK(RETURN(EXPRESSION(TYPE !nothing; !nothing)))); EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT64; !a)))); RETURN(EXPRESSION(TYPE !nothing; !nothing)))))))")
    }

    @Test
    fun whenStatementTests()
    {
        /*
        let list := mut [] : int?
        let x := list @ 0
        when x is begin
            nothing do ignore(x)
            mut[nothing] do ignore(x)
            default do ignore(x)
        end
        */
        checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE !nothing", "TYPE INT64")),
            """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!list",
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
          "lineIndex" : 13
        }
      }, {
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 18
        },
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 22
          },
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 22
            }
          }, {
            "name" : "!nothing",
            "type" : "TYPE"
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!x",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!list",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 10
        }
      }, {
        "name" : "ELEM_ACCESS",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 15
        }
      }, {
        "name" : "#0",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 17
        }
      } ]
    } ]
  }, {
    "type" : "WHEN",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!x",
        "type" : "TOKEN",
        "location" : {
          "line" : 3,
          "lineIndex" : 6
        }
      } ]
    }, {
      "name" : "!nothing",
      "type" : "TYPE",
      "location" : {
        "line" : 4,
        "lineIndex" : 5
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 4,
            "lineIndex" : 16
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 4,
            "lineIndex" : 22
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!x",
              "type" : "TOKEN",
              "location" : {
                "line" : 4,
                "lineIndex" : 23
              }
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "location" : {
        "line" : 5,
        "lineIndex" : 5
      },
      "components" : [ {
        "name" : "!nothing",
        "type" : "TYPE",
        "location" : {
          "line" : 5,
          "lineIndex" : 9
        }
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 5,
            "lineIndex" : 21
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 5,
            "lineIndex" : 27
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!x",
              "type" : "TOKEN",
              "location" : {
                "line" : 5,
                "lineIndex" : 28
              }
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "ELSE",
      "type" : "TOKEN",
      "location" : {
        "line" : 6,
        "lineIndex" : 5
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 6,
            "lineIndex" : 16
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 6,
            "lineIndex" : 22
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!x",
              "type" : "TOKEN",
              "location" : {
                "line" : 6,
                "lineIndex" : 23
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)))); !list; EXPRESSION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)))); MUTABLE; LIST(TYPE EITHER; (TYPE INT64; TYPE !nothing)))); DECLARATION(TYPE EITHER; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)); TYPE !nothing); !x; EXPRESSION(TYPE EITHER; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)); TYPE !nothing); !list; LIST|ELEM_ACCESS|INT64; #0)); WHEN(EXPRESSION(!x); TYPE !nothing; CODE_BLOCK(EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE !nothing; !x))))); TYPE MUTABLE; (TYPE !nothing); CODE_BLOCK(EXPRESSION(!ignore; APPLY; METHOD_CALL(EXPRESSION(!x)))); ELSE; CODE_BLOCK(EXPRESSION(!ignore; APPLY; METHOD_CALL(EXPRESSION(!x))))))")

        /*
        let list := mut [] : int?
        let x := list @ 0
        when x is begin
            mut[nothing] do ignore(x)
            nothing do ignore(x)
            default do ignore(x)
        end
         */

        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!list",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 13
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "MUTABLE",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 21
        }
      }, {
        "type" : "LIST",
        "location" : {
          "line" : 1,
          "lineIndex" : 26
        },
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "location" : {
            "line" : 1,
            "lineIndex" : 30
          },
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE",
            "location" : {
              "line" : 1,
              "lineIndex" : 30
            }
          }, {
            "name" : "!nothing",
            "type" : "TYPE"
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!x",
      "type" : "TOKEN",
      "location" : {
        "line" : 2,
        "lineIndex" : 13
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!list",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 18
        }
      }, {
        "name" : "ELEM_ACCESS",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 23
        }
      }, {
        "name" : "#0",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 25
        }
      } ]
    } ]
  }, {
    "type" : "WHEN",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!x",
        "type" : "TOKEN",
        "location" : {
          "line" : 3,
          "lineIndex" : 14
        }
      } ]
    }, {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "location" : {
        "line" : 4,
        "lineIndex" : 13
      },
      "components" : [ {
        "name" : "!nothing",
        "type" : "TYPE",
        "location" : {
          "line" : 4,
          "lineIndex" : 17
        }
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 4,
            "lineIndex" : 29
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 4,
            "lineIndex" : 35
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!x",
              "type" : "TOKEN",
              "location" : {
                "line" : 4,
                "lineIndex" : 36
              }
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "!nothing",
      "type" : "TYPE",
      "location" : {
        "line" : 5,
        "lineIndex" : 13
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 5,
            "lineIndex" : 24
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 5,
            "lineIndex" : 30
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!x",
              "type" : "TOKEN",
              "location" : {
                "line" : 5,
                "lineIndex" : 31
              }
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "ELSE",
      "type" : "TOKEN",
      "location" : {
        "line" : 6,
        "lineIndex" : 13
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 6,
            "lineIndex" : 24
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 6,
            "lineIndex" : 30
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!x",
              "type" : "TOKEN",
              "location" : {
                "line" : 6,
                "lineIndex" : 31
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)))); !list; EXPRESSION(TYPE MUTABLE; (TYPE LIST; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)))); MUTABLE; LIST(TYPE EITHER; (TYPE INT64; TYPE !nothing)))); DECLARATION(TYPE EITHER; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)); TYPE !nothing); !x; EXPRESSION(TYPE EITHER; (TYPE MUTABLE; (TYPE EITHER; (TYPE INT64; TYPE !nothing)); TYPE !nothing); !list; LIST|ELEM_ACCESS|INT64; #0)); WHEN(EXPRESSION(!x); TYPE MUTABLE; (TYPE !nothing); CODE_BLOCK(EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE MUTABLE; (TYPE !nothing); !x))))); TYPE !nothing; CODE_BLOCK(EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE !nothing; !x))))); TYPE ELSE; (TYPE MUTABLE; (TYPE INT64)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE MUTABLE; (TYPE INT64); !x)))))))")

        /*
        let a : either[text, int] := 2
        when a is int do
            ignore(a)
         */
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
        "name" : "#2",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 30
        }
      } ]
    } ]
  }, {
    "type" : "WHEN",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    }, {
      "name" : "INT64",
      "type" : "TYPE",
      "location" : {
        "line" : 2,
        "lineIndex" : 11
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!ignore",
          "type" : "TOKEN",
          "location" : {
            "line" : 3,
            "lineIndex" : 5
          }
        }, {
          "name" : "APPLY",
          "type" : "TOKEN",
          "location" : {
            "line" : 3,
            "lineIndex" : 11
          }
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!a",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 12
              }
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE STRING; TYPE INT64); !a; EXPRESSION(TYPE INT64; #2)); WHEN(EXPRESSION(!a); TYPE INT64; CODE_BLOCK(EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT64; !a)))))))")

    checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "!func",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "location" : {
          "line" : 1,
          "lineIndex" : 13
        },
        "components" : [ {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "DECLARATION",
            "components" : [ {
              "name" : "EITHER",
              "type" : "TYPE",
              "location" : {
                "line" : 2,
                "lineIndex" : 13
              },
              "components" : [ {
                "name" : "INT64",
                "type" : "TYPE",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 20
                }
              }, {
                "name" : "STRING",
                "type" : "TYPE",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 24
                }
              } ]
            }, {
              "name" : "!a",
              "type" : "TOKEN",
              "location" : {
                "line" : 2,
                "lineIndex" : 9
              }
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#1",
                "type" : "TOKEN",
                "location" : {
                  "line" : 2,
                  "lineIndex" : 33
                }
              } ]
            } ]
          }, {
            "type" : "DECLARATION",
            "components" : [ {
              "name" : "INT64",
              "type" : "TYPE",
              "location" : {
                "line" : 3,
                "lineIndex" : 13
              }
            }, {
              "name" : "!b",
              "type" : "TOKEN",
              "location" : {
                "line" : 3,
                "lineIndex" : 9
              }
            } ]
          }, {
            "type" : "WHEN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!a",
                "type" : "TOKEN",
                "location" : {
                  "line" : 4,
                  "lineIndex" : 10
                }
              } ]
            }, {
              "name" : "INT64",
              "type" : "TYPE",
              "location" : {
                "line" : 5,
                "lineIndex" : 9
              }
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "!b",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 5,
                      "lineIndex" : 16
                    }
                  } ]
                }, {
                  "name" : "ASSIGN",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 5,
                    "lineIndex" : 18
                  }
                }, {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#1",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 5,
                      "lineIndex" : 21
                    }
                  } ]
                } ]
              } ]
            }, {
              "name" : "STRING",
              "type" : "TYPE",
              "location" : {
                "line" : 6,
                "lineIndex" : 9
              }
            }, {
              "type" : "CODE_BLOCK",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "!b",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 6,
                      "lineIndex" : 17
                    }
                  } ]
                }, {
                  "name" : "ASSIGN",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 6,
                    "lineIndex" : 19
                  }
                }, {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#2",
                    "type" : "TOKEN",
                    "location" : {
                      "line" : 6,
                      "lineIndex" : 22
                    }
                  } ]
                } ]
              } ]
            } ]
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!ignore",
              "type" : "TOKEN",
              "location" : {
                "line" : 8,
                "lineIndex" : 5
              }
            }, {
              "name" : "APPLY",
              "type" : "TOKEN",
              "location" : {
                "line" : 8,
                "lineIndex" : 11
              }
            }, {
              "type" : "METHOD_CALL",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "!b",
                  "type" : "TOKEN",
                  "location" : {
                    "line" : 8,
                    "lineIndex" : 12
                  }
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing)); METHOD(TYPE !nothing; CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT64; TYPE STRING); !a; EXPRESSION(TYPE INT64; #1)); DECLARATION(TYPE INT64; !b); WHEN(EXPRESSION(!a); TYPE INT64; CODE_BLOCK(EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT64; !b); ASSIGN; EXPRESSION(TYPE INT64; #1))); TYPE ELSE; (TYPE STRING); CODE_BLOCK(EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT64; !b); ASSIGN; EXPRESSION(TYPE INT64; #2)))); EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT64; !b)))))))))")

        checkResult(createExceptions(TypesExhaustedException(NULL_LOCATION)),"""{
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
          "lineIndex" : 20
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
        "name" : "#1",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 29
        }
      } ]
    } ]
  }, {
    "type" : "WHEN",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN",
        "location" : {
          "line" : 2,
          "lineIndex" : 6
        }
      } ]
    }, {
      "name" : "INT64",
      "type" : "TYPE",
      "location" : {
        "line" : 3,
        "lineIndex" : 5
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!nothing",
          "type" : "TOKEN",
          "location" : {
            "line" : 3,
            "lineIndex" : 12
          }
        } ]
      } ]
    }, {
      "name" : "STRING",
      "type" : "TYPE",
      "location" : {
        "line" : 4,
        "lineIndex" : 5
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!nothing",
          "type" : "TOKEN",
          "location" : {
            "line" : 4,
            "lineIndex" : 13
          }
        } ]
      } ]
    }, {
      "name" : "ELSE",
      "type" : "TOKEN",
      "location" : {
        "line" : 5,
        "lineIndex" : 5
      }
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!nothing",
          "type" : "TOKEN",
          "location" : {
            "line" : 5,
            "lineIndex" : 16
          }
        } ]
      } ]
    } ]
  } ]
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT64; TYPE STRING); !a; EXPRESSION(TYPE INT64; #1)); WHEN(EXPRESSION(!a); TYPE INT64; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing)); TYPE STRING; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing)); TYPE ELSE; (TYPE EITHER); CODE_BLOCK(EXPRESSION(!nothing))))")
    }

    companion object {
        private const val TYPE = "TYPE "

        @JvmStatic
        @BeforeAll
        fun init() {
            Main.filename = "code.wiles"
        }
    }
}