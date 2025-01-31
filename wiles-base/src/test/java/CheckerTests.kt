
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import wiles.checker.Checker
import wiles.checker.exceptions.*
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.constants.ErrorMessages.CONFLICTING_TYPES_FOR_IDENTIFIER_ERROR
import wiles.shared.constants.ErrorMessages.EXPECTED_VALUE_FOR_IDENTIFIER_ERROR
import wiles.shared.constants.ErrorMessages.NO_MATCH_FOR_ERROR
import wiles.shared.constants.ErrorMessages.TOO_MANY_VALUES_PROVIDED_ERROR
import wiles.shared.constants.Types.INT_ID
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
    fun variableContextTest()
    {
        /*
            let var a := 100
            let func := fun(a := 200) do nothing
         */
        checkResult(createExceptions(VariableAlreadyDeclaredException(NULL_LOCATION)),
            """
                {
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "name": "VARIABLE",
      "type": "DECLARATION",
      "components": [
        {
          "name": "!a",
          "type": "TOKEN",
          "location": {
            "line": 1,
            "lineIndex": 9
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "#100",
              "type": "TOKEN",
              "location": {
                "line": 1,
                "lineIndex": 14
              }
            }
          ]
        }
      ]
    },
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!func",
          "type": "TOKEN",
          "location": {
            "line": 2,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "METHOD",
              "location": {
                "line": 2,
                "lineIndex": 13
              },
              "components": [
                {
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "!a",
                      "type": "TOKEN",
                      "location": {
                        "line": 2,
                        "lineIndex": 17
                      }
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "#200",
                          "type": "TOKEN",
                          "location": {
                            "line": 2,
                            "lineIndex": 22
                          }
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!nothing",
                          "type": "TOKEN",
                          "location": {
                            "line": 2,
                            "lineIndex": 30
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
            """,
            "CODE_BLOCK(DECLARATION VARIABLE; (TYPE INT; !a; EXPRESSION(TYPE INT; #100)); DECLARATION(!func; EXPRESSION(METHOD(DECLARATION(!a; EXPRESSION(#200)); CODE_BLOCK(EXPRESSION(!nothing))))))")
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
}""", "CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !x; EXPRESSION(TYPE INT; #10)); DECLARATION ANON_ARG; (TYPE INT; !y; EXPRESSION(TYPE INT; #10)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !x; EXPRESSION(TYPE INT; #10)); DECLARATION ANON_ARG; (TYPE INT; !y; EXPRESSION(TYPE INT; #10)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

        // let func : fun[] := fun(x : int) do nothing
        checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE METHOD; (METHOD(TYPE !nothing))","TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !x)))")),
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !x))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !x); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

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
}""", "CODE_BLOCK(DECLARATION(TYPE INT; !a; EXPRESSION(TYPE INT; #10)))")

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

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION, TYPE + INT_ID, TYPE + STRING_ID)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
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
}""", "CODE_BLOCK(DECLARATION(TYPE INT; !a; EXPRESSION(TYPE STRING; @10)))")

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
}""","CODE_BLOCK(DECLARATION(TYPE INT; !a; EXPRESSION(TYPE INT; #1)); DECLARATION(!a; EXPRESSION(#2)))")

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
        "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE !nothing; !a; EXPRESSION(!nothing)); DECLARATION(TYPE EITHER; (TYPE INT; TYPE !nothing); !b; EXPRESSION(TYPE !nothing; !a)))")

    checkResult(createExceptions(UsedBeforeInitializationException(NULL_LOCATION)),
"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
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
}""", "CODE_BLOCK(DECLARATION(TYPE INT; !a); DECLARATION(!b; EXPRESSION(!a)))")

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
        checkResult(createExceptions(UnknownIdentifierException(NULL_LOCATION)),"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!abc",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 1
        }
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN",
      "location" : {
        "line" : 1,
        "lineIndex" : 5
      }
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#100",
        "type" : "TOKEN",
        "location" : {
          "line" : 1,
          "lineIndex" : 8
        }
      } ]
    } ]
  } ]
}""","CODE_BLOCK(EXPRESSION(EXPRESSION(!abc); ASSIGN; EXPRESSION(#100)))")

        checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION,
            CONFLICTING_TYPES_FOR_IDENTIFIER_ERROR.format("TYPE ANYTHING","TYPE EITHER; (TYPE INT; TYPE !nothing)","text"))),"""{
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
        "name" : "INT",
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
      "name" : "!write_line",
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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT; TYPE !nothing); !a; EXPRESSION(TYPE INT; #2)); EXPRESSION(!write_line; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE EITHER; (TYPE INT; TYPE !nothing); !a))))))")

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
}""","CODE_BLOCK(EXPRESSION(TYPE INT; #1))")

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
}""","CODE_BLOCK(DECLARATION(TYPE INT; !a; EXPRESSION(TYPE INT; !nothing|UNARY_PLUS|INT; #2)))")


        checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE EITHER; (TYPE INT; TYPE !nothing)","TYPE INT")),
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
        "name" : "INT",
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
        "name" : "PLUS",
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
}""", "CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT; TYPE !nothing); !a; EXPRESSION(TYPE INT; #2)); DECLARATION(!b; EXPRESSION(!a; PLUS; #3)))")

    checkResult(null,
        """{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
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
}""", "CODE_BLOCK(DECLARATION(TYPE INT; !a); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT; !a); ASSIGN; EXPRESSION(TYPE INT; #3)))")


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
}""", "CODE_BLOCK(DECLARATION(TYPE INT; !a; EXPRESSION(TYPE INT; #2)); EXPRESSION(EXPRESSION(!a); ASSIGN; EXPRESSION(#3)))")

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
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE INT; !a; EXPRESSION(TYPE INT; #2)); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT; !a); ASSIGN; EXPRESSION(TYPE INT; #3)))")

    checkResult(createExceptions(WrongOperationException(NULL_LOCATION,"TYPE INT","TYPE STRING")),
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
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE INT; !a; EXPRESSION(TYPE INT; #2)); EXPRESSION(EXPRESSION(TYPE INT; !a); ASSIGN; EXPRESSION(TYPE STRING; @3)))")

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
        "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION VARIABLE; (TYPE EITHER; (TYPE INT; TYPE STRING); !a; EXPRESSION(TYPE INT; #2)); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE EITHER; (TYPE INT; TYPE STRING); !a); ASSIGN; EXPRESSION(TYPE STRING; @3)))")

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
        "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE STRING; TYPE INT); !a; EXPRESSION(TYPE STRING; @3)); DECLARATION(TYPE STRING; !b; EXPRESSION(TYPE STRING; @hi; STRING|PLUS|ANYTHING; !a)))")

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
        "name" : "DECIMAL",
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
        "name" : "INT",
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
        "name" : "DECIMAL",
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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE DECIMAL; TYPE STRING); !a; EXPRESSION(TYPE STRING; @a)); DECLARATION(TYPE EITHER; (TYPE INT; TYPE STRING); !b; EXPRESSION(TYPE INT; #1)); DECLARATION(TYPE EITHER; (TYPE DECIMAL; TYPE STRING); !c); EXPRESSION(TYPE !nothing; EXPRESSION(TYPE EITHER; (TYPE DECIMAL; TYPE STRING); !c); ASSIGN; EXPRESSION(TYPE EITHER; (TYPE DECIMAL; TYPE STRING); !a; ANYTHING|PLUS|ANYTHING; !b)))")

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
}""","CODE_BLOCK(DECLARATION(!a; EXPRESSION(LIST(TYPE ANYTHING; EXPRESSION(TYPE INT; #4); EXPRESSION(TYPE !nothing; !nothing)))))")

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
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE INT); !a; EXPRESSION(TYPE LIST; (TYPE INT); LIST(TYPE INT; EXPRESSION(TYPE INT; #1); EXPRESSION(TYPE INT; #2); EXPRESSION(TYPE INT; #3)))))")

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
}""","CODE_BLOCK(DECLARATION(!a; EXPRESSION(LIST(EXPRESSION(TYPE INT; #1); EXPRESSION(TYPE INT; #2); EXPRESSION(TYPE STRING; @3)))))")

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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE LIST; (TYPE EITHER; (TYPE INT; TYPE !nothing)); !a; EXPRESSION(TYPE LIST; (TYPE EITHER; (TYPE INT; TYPE !nothing)); LIST(TYPE EITHER; (TYPE INT; TYPE !nothing); EXPRESSION(TYPE INT; #1); EXPRESSION(TYPE INT; #2); EXPRESSION(TYPE !nothing; !nothing)))))")
        /*
        let a := mut [] : int
        let b := a.get(at := 0)
         */
    }

    @Test
    fun methodCallTest()
    {
        //write_line([1,2,3].fun(arg list: list[int]) do yield 10)
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!write_line",
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
                "name" : "INT",
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
}""","CODE_BLOCK(EXPRESSION(TYPE !nothing; !write_line; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE INT; METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE LIST; (TYPE INT); !list); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT; #10)))); METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!list; ASSIGN; EXPRESSION(TYPE LIST; (TYPE INT); LIST(TYPE INT; EXPRESSION(TYPE INT; #1); EXPRESSION(TYPE INT; #2); EXPRESSION(TYPE INT; #3))))))))))")


        //write_line([1,2,3].size)
        checkResult(null,"""{
  "parsed" : true,
  "type" : "CODE_BLOCK",
  "components" : [ {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!write_line",
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
}""","CODE_BLOCK(EXPRESSION(TYPE !nothing; !write_line; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE INT; !size; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE LIST; (TYPE INT); LIST(TYPE INT; EXPRESSION(TYPE INT; #1); EXPRESSION(TYPE INT; #2); EXPRESSION(TYPE INT; #3))))))))))")

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
}""","CODE_BLOCK(DECLARATION(TYPE INT; !a; EXPRESSION(TYPE INT; !modulo; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!x; ASSIGN; EXPRESSION(TYPE INT; #10)); EXPRESSION(!y; ASSIGN; EXPRESSION(TYPE INT; #3))))))")

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
}""", "CODE_BLOCK(EXPRESSION(TYPE !nothing; !write; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE STRING; !as_text; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT; #2))))))))")

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
            "name" : "INT",
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(TYPE !nothing; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT; #10)); EXPRESSION(!c; ASSIGN; EXPRESSION(TYPE INT; #40)))))")

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
            "name" : "INT",
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(TYPE !nothing; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!d; ASSIGN; EXPRESSION(TYPE INT; #40)); EXPRESSION(!c; ASSIGN; EXPRESSION(TYPE INT; #30)); EXPRESSION(!b; ASSIGN; EXPRESSION(TYPE INT; #20)); EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT; #10)))))")

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
            "name" : "INT",
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(TYPE !nothing; !func; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT; #10)); EXPRESSION(!c; ASSIGN; EXPRESSION(TYPE INT; #30)); EXPRESSION(!d; ASSIGN; EXPRESSION(TYPE INT; #40)))))")

    checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION,EXPECTED_VALUE_FOR_IDENTIFIER_ERROR.format("a"))),
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
            "name" : "INT",
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(!func; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE INT; #30); EXPRESSION(!b; ASSIGN; EXPRESSION(TYPE INT; #20)))))))")

        checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION,EXPECTED_VALUE_FOR_IDENTIFIER_ERROR.format("a"))),
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
            "name" : "INT",
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)))); METHOD(TYPE !nothing; DECLARATION(TYPE INT; !a); DECLARATION(TYPE INT; !b; EXPRESSION(TYPE INT; #2)); DECLARATION ANON_ARG; (TYPE INT; !c); DECLARATION ANON_ARG; (TYPE INT; !d; EXPRESSION(TYPE INT; #4)); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(!func; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE INT; #10); EXPRESSION(TYPE INT; #20); EXPRESSION(TYPE INT; #30))))))")

    /*
    let func := fun() do nothing
    func(y := 10)
     */
    checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION,NO_MATCH_FOR_ERROR.format("y"))),
        """{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!func",
          "type": "TOKEN",
          "location": {
            "line": 1,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "METHOD",
              "location": {
                "line": 1,
                "lineIndex": 13
              },
              "components": [
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!nothing",
                          "type": "TOKEN",
                          "location": {
                            "line": 1,
                            "lineIndex": 22
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "type": "EXPRESSION",
      "components": [
        {
          "name": "!func",
          "type": "TOKEN",
          "location": {
            "line": 2,
            "lineIndex": 1
          }
        },
        {
          "name": "APPLY",
          "type": "TOKEN",
          "location": {
            "line": 2,
            "lineIndex": 5
          }
        },
        {
          "type": "METHOD_CALL",
          "components": [
            {
              "type": "EXPRESSION",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!y",
                      "type": "TOKEN",
                      "location": {
                        "line": 2,
                        "lineIndex": 6
                      }
                    }
                  ]
                },
                {
                  "name": "ASSIGN",
                  "type": "TOKEN",
                  "location": {
                    "line": 2,
                    "lineIndex": 8
                  }
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#10",
                      "type": "TOKEN",
                      "location": {
                        "line": 2,
                        "lineIndex": 11
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing)); METHOD(TYPE !nothing; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(!func; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(!y; ASSIGN; EXPRESSION(TYPE INT; #10)))))))")


    /*
    let func := fun(arg x : int) do nothing
    func(10,20)
     */
    checkResult(createExceptions(CannotCallMethodException(NULL_LOCATION,TOO_MANY_VALUES_PROVIDED_ERROR.format("y"))),
        """{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!func",
          "type": "TOKEN",
          "location": {
            "line": 1,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "METHOD",
              "location": {
                "line": 1,
                "lineIndex": 13
              },
              "components": [
                {
                  "name": "ANON_ARG",
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE",
                      "location": {
                        "line": 1,
                        "lineIndex": 25
                      }
                    },
                    {
                      "name": "!x",
                      "type": "TOKEN",
                      "location": {
                        "line": 1,
                        "lineIndex": 21
                      }
                    }
                  ]
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!nothing",
                          "type": "TOKEN",
                          "location": {
                            "line": 1,
                            "lineIndex": 33
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "type": "EXPRESSION",
      "components": [
        {
          "name": "!func",
          "type": "TOKEN",
          "location": {
            "line": 2,
            "lineIndex": 1
          }
        },
        {
          "name": "APPLY",
          "type": "TOKEN",
          "location": {
            "line": 2,
            "lineIndex": 5
          }
        },
        {
          "type": "METHOD_CALL",
          "components": [
            {
              "type": "EXPRESSION",
              "components": [
                {
                  "name": "#10",
                  "type": "TOKEN",
                  "location": {
                    "line": 2,
                    "lineIndex": 6
                  }
                }
              ]
            },
            {
              "type": "EXPRESSION",
              "components": [
                {
                  "name": "#20",
                  "type": "TOKEN",
                  "location": {
                    "line": 2,
                    "lineIndex": 9
                  }
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !x))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !x))); METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !x); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))); EXPRESSION(!func; APPLY; METHOD_CALL(TYPE METHOD_CALL; (METHOD_CALL(EXPRESSION(TYPE INT; #10); EXPRESSION(TYPE INT; #20))))))")
    }

    @Test
    fun methodsTest()
    {
        //let func := fun() -> int do panic()
        checkResult(null,"""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!func",
          "type": "TOKEN",
          "location": {
            "line": 1,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "METHOD",
              "location": {
                "line": 1,
                "lineIndex": 13
              },
              "components": [
                {
                  "name": "INT",
                  "type": "TYPE",
                  "location": {
                    "line": 1,
                    "lineIndex": 22
                  }
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!panic",
                          "type": "TOKEN",
                          "location": {
                            "line": 1,
                            "lineIndex": 29
                          }
                        },
                        {
                          "name": "APPLY",
                          "type": "TOKEN",
                          "location": {
                            "line": 1,
                            "lineIndex": 34
                          }
                        },
                        {
                          "type": "METHOD_CALL"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE INT)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT)); METHOD(TYPE INT; CODE_BLOCK(EXPRESSION(TYPE !nothing; !panic; METHOD|APPLY|METHOD_CALL; METHOD_CALL))))))")

        //let a := fun(a : A as B, b : B as A) do nothing

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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE INT)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT)); METHOD(TYPE INT; CODE_BLOCK(IF(EXPRESSION(TYPE BOOLEAN; !true); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT; #1))); ELSE; CODE_BLOCK(RETURN(EXPRESSION(TYPE INT; #2)))))))))")

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
}""","CODE_BLOCK(DECLARATION(!func; EXPRESSION(METHOD(TYPE INT; CODE_BLOCK(IF(EXPRESSION(TYPE BOOLEAN; !true); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT; #1))); ELSE; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))))")

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
          "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE INT)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT)); METHOD(TYPE INT; CODE_BLOCK(RETURN(EXPRESSION(TYPE INT; #10)))))))")

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

        //let func : fun[a : int] := fun(a := maybe(20)) do nothing



        //new one
    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !a); DECLARATION ANON_ARG; (TYPE INT; !b)))","TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !b); DECLARATION ANON_ARG; (TYPE INT; !a)))")),
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
            "name" : "INT",
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
            "name" : "INT",
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
            "name" : "INT",
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
            "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !a); DECLARATION ANON_ARG; (TYPE INT; !b))); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !b); DECLARATION ANON_ARG; (TYPE INT; !a))); METHOD(TYPE !nothing; DECLARATION ANON_ARG; (TYPE INT; !b); DECLARATION ANON_ARG; (TYPE INT; !a); CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing))))))")

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
}""", "CODE_BLOCK(DECLARATION(!func; EXPRESSION(METHOD(CODE_BLOCK(RETURN(EXPRESSION(TYPE STRING; @10)); RETURN(EXPRESSION(TYPE INT; #10)))))))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE INT","TYPE STRING")),"""{
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
          "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(!func; EXPRESSION(METHOD(TYPE INT; CODE_BLOCK(RETURN(EXPRESSION(TYPE STRING; @10)))))))")
    }

    @Test
    fun closureTest()
    {
        /*
        let create_sum := fun(arg a : int) do
            yield fun(arg b : int) do
                yield a + b

        let add_5 := create_sum(5)
        let number := add_5(5)
         */

        checkResult(null,"""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!create_sum",
          "type": "TOKEN",
          "location": {
            "line": 1,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "METHOD",
              "location": {
                "line": 1,
                "lineIndex": 19
              },
              "components": [
                {
                  "name": "ANON_ARG",
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE",
                      "location": {
                        "line": 1,
                        "lineIndex": 31
                      }
                    },
                    {
                      "name": "!a",
                      "type": "TOKEN",
                      "location": {
                        "line": 1,
                        "lineIndex": 27
                      }
                    }
                  ]
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "RETURN",
                      "components": [
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "type": "METHOD",
                              "location": {
                                "line": 2,
                                "lineIndex": 11
                              },
                              "components": [
                                {
                                  "name": "ANON_ARG",
                                  "type": "DECLARATION",
                                  "components": [
                                    {
                                      "name": "INT",
                                      "type": "TYPE",
                                      "location": {
                                        "line": 2,
                                        "lineIndex": 23
                                      }
                                    },
                                    {
                                      "name": "!b",
                                      "type": "TOKEN",
                                      "location": {
                                        "line": 2,
                                        "lineIndex": 19
                                      }
                                    }
                                  ]
                                },
                                {
                                  "type": "CODE_BLOCK",
                                  "components": [
                                    {
                                      "type": "RETURN",
                                      "components": [
                                        {
                                          "type": "EXPRESSION",
                                          "components": [
                                            {
                                              "name": "!a",
                                              "type": "TOKEN",
                                              "location": {
                                                "line": 3,
                                                "lineIndex": 15
                                              }
                                            },
                                            {
                                              "name": "PLUS",
                                              "type": "TOKEN",
                                              "location": {
                                                "line": 3,
                                                "lineIndex": 17
                                              }
                                            },
                                            {
                                              "name": "!b",
                                              "type": "TOKEN",
                                              "location": {
                                                "line": 3,
                                                "lineIndex": 19
                                              }
                                            }
                                          ]
                                        }
                                      ]
                                    }
                                  ]
                                }
                              ]
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!add_5",
          "type": "TOKEN",
          "location": {
            "line": 5,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!create_sum",
              "type": "TOKEN",
              "location": {
                "line": 5,
                "lineIndex": 14
              }
            },
            {
              "name": "APPLY",
              "type": "TOKEN",
              "location": {
                "line": 5,
                "lineIndex": 24
              }
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#5",
                      "type": "TOKEN",
                      "location": {
                        "line": 5,
                        "lineIndex": 25
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    },
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "!number",
          "type": "TOKEN",
          "location": {
            "line": 6,
            "lineIndex": 5
          }
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!add_5",
              "type": "TOKEN",
              "location": {
                "line": 6,
                "lineIndex": 15
              }
            },
            {
              "name": "APPLY",
              "type": "TOKEN",
              "location": {
                "line": 6,
                "lineIndex": 20
              }
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#5",
                      "type": "TOKEN",
                      "location": {
                        "line": 6,
                        "lineIndex": 21
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE METHOD; (METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b))); DECLARATION ANON_ARG; (TYPE INT; !a))); !create_sum; EXPRESSION(TYPE METHOD; (METHOD(TYPE METHOD; (METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b))); DECLARATION ANON_ARG; (TYPE INT; !a))); METHOD(TYPE METHOD; (METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b))); DECLARATION ANON_ARG; (TYPE INT; !a); CODE_BLOCK(RETURN(EXPRESSION(TYPE METHOD; (METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b))); METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b); CODE_BLOCK(RETURN(EXPRESSION(TYPE INT; !a; INT|PLUS|INT; !b)))))))))); DECLARATION(TYPE METHOD; (METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b))); !add_5; EXPRESSION(TYPE METHOD; (METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !b))); !create_sum; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!a; ASSIGN; EXPRESSION(TYPE INT; #5))))); DECLARATION(TYPE INT; !number; EXPRESSION(TYPE INT; !add_5; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!b; ASSIGN; EXPRESSION(TYPE INT; #5))))))")
    }

    @Test
    fun forTests()
    {
        /*
        let list : list[int] or list[text] := [1,2,3]
        for i in list from 0 to 2 do
            write_line(i.as_text)
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
          "name" : "INT",
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
          "name" : "!write_line",
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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE LIST; (TYPE INT); TYPE LIST; (TYPE STRING)); !list; EXPRESSION(TYPE LIST; (TYPE INT); LIST(TYPE INT; EXPRESSION(TYPE INT; #1); EXPRESSION(TYPE INT; #2); EXPRESSION(TYPE INT; #3)))); FOR(TYPE EITHER; (TYPE INT; TYPE STRING); !i; IN; EXPRESSION(TYPE EITHER; (TYPE LIST; (TYPE INT); TYPE LIST; (TYPE STRING)); !list); FROM; EXPRESSION(TYPE INT; #0); TO; EXPRESSION(TYPE INT; #2); CODE_BLOCK(EXPRESSION(TYPE !nothing; !write_line; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE STRING; !as_text; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE EITHER; (TYPE INT; TYPE STRING); !i))))))))))")
    }

    @Test
    fun whileTests()
    {
        /*
        while true do
            let text := "hi!"
        write_line(text)
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
      "name" : "!write_line",
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
}""","CODE_BLOCK(WHILE(EXPRESSION(TYPE BOOLEAN; !true); CODE_BLOCK(DECLARATION(TYPE STRING; !text; EXPRESSION(TYPE STRING; @hi!)))); EXPRESSION(!write_line; APPLY; METHOD_CALL(EXPRESSION(!text))))")

    checkResult(createExceptions(ConflictingTypeDefinitionException(NULL_LOCATION,"TYPE BOOLEAN","TYPE INT")),"""{
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
}""","CODE_BLOCK(WHILE(EXPRESSION(TYPE INT; #1); CODE_BLOCK(EXPRESSION(!nothing))))")
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
              "name" : "INT",
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
}""", "CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing)); METHOD(TYPE !nothing; CODE_BLOCK(DECLARATION(TYPE INT; !a); IF(EXPRESSION(TYPE BOOLEAN; #1; INT|LARGER|INT; #2); CODE_BLOCK(EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT; !a); ASSIGN; EXPRESSION(TYPE INT; #1))); ELSE; CODE_BLOCK(RETURN(EXPRESSION(TYPE !nothing; !nothing)))); EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT; !a)))); RETURN(EXPRESSION(TYPE !nothing; !nothing)))))))")
    }

    @Test
    fun whenStatementTests()
    {
        /*
        let a := maybe(10)
        when a is nothing do panic()
        let b := a + 10
         */

        /*
        let list := mut [] : mut[int?]
        let x := maybe(list.get(at := 0))
        when x begin
            is nothing do ignore(x)
            is mut[nothing] do ignore(x)
            default do ignore(x)
        end
        */

        /*
    let list := mut [] : mut[int?]
    let x := maybe(list.get(at := 0))
    when x begin
        is mut[nothing] do ignore(x)
        is nothing do ignore(x)
        default do ignore(x)
    end
     */

        /*
        let a : text or int := 2
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
        "name" : "INT",
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
      "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE STRING; TYPE INT); !a; EXPRESSION(TYPE INT; #2)); WHEN(EXPRESSION(!a); TYPE INT; CODE_BLOCK(EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT; !a)))))))")

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
                "name" : "INT",
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
              "name" : "INT",
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
              "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE !nothing)); !func; EXPRESSION(TYPE METHOD; (METHOD(TYPE !nothing)); METHOD(TYPE !nothing; CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT; TYPE STRING); !a; EXPRESSION(TYPE INT; #1)); DECLARATION(TYPE INT; !b); WHEN(EXPRESSION(!a); TYPE INT; CODE_BLOCK(EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT; !b); ASSIGN; EXPRESSION(TYPE INT; #1))); TYPE ELSE; (TYPE STRING); CODE_BLOCK(EXPRESSION(TYPE !nothing; EXPRESSION(TYPE INT; !b); ASSIGN; EXPRESSION(TYPE INT; #2)))); EXPRESSION(TYPE !nothing; !ignore; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!elem; ASSIGN; EXPRESSION(TYPE INT; !b)))))))))")

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
        "name" : "INT",
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
      "name" : "INT",
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
}""","CODE_BLOCK(DECLARATION(TYPE EITHER; (TYPE INT; TYPE STRING); !a; EXPRESSION(TYPE INT; #1)); WHEN(EXPRESSION(!a); TYPE INT; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing)); TYPE STRING; CODE_BLOCK(EXPRESSION(TYPE !nothing; !nothing)); TYPE ELSE; (TYPE EITHER); CODE_BLOCK(EXPRESSION(!nothing))))")
    }

    @Test
    fun genericsTest()
    {
        /*
        let list : mut[collection[int,text]] := mut ["1"]
        list.remove(at := 0)
         */

        /*
        let list := mut [1,2,3]
        let b : int? := 4
        list.add(b)
         */

        /*
        let list := mut [1,2,3]
        list.add("hi!")
         */

        /*
        let func1 := fun(elem1 : anything? as T, elem2 : T)
        begin
            let func2 := fun(elem3 : anything? as T, elem4 : T) do nothing
        end
         */

        /*
        let add_func := fun(arg list : list[anything? as T], arg elem : T) -> list[T]
        begin
            yield list
        end
        write_line([1,2].add_func(3))
         */

        /*
        typedef number := int or DECIMAL
        let add_func := fun(arg x :number as T, arg y : T) -> T
        begin
            yield x + y
        end
        write_line(2.add_func(3))
         */

        /*
        let run_func := fun(arg func : fun[->anything? as T]) -> T
            do yield func()

        write_line(run_func(do yield 10)+20)
        */

        /*
        let func : fun[arg x : anything as T -> T]
        func := fun(arg x : int) -> int do yield x
         */

        /*
        let func := fun(x : T, y : anything? as T) do nothing
        func(x := 2, y := maybe(5))
         */

   /*
    let list := mut [1,2,3]
    list.add(at := "hi", 4)
    */

    /*
    let list := mut [1,2,3]
    list.add(at := 2, "4")
     */
    }

    companion object {
        private const val TYPE = "TYPE "
    }
}