
import org.junit.jupiter.api.Test
import wiles.interpreter.Interpreter
import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import java.math.BigInteger
import java.math.MathContext.DECIMAL128
import java.util.*

class InterpreterTests {
    private fun assertVar(vars : InterpreterVariableMap, name : String, value : Any?)
    {
        assert(vars[name]?.value == value)
    }

    private fun getVars(code : String) : InterpreterVariableMap
    {
        val interpreter = Interpreter(code, true, "code.wiles",
            InterpreterContext(Scanner(System.`in`), StringBuilder()))
        interpreter.interpret()
        return interpreter.newVars
    }

    @Test
    fun expressionTests()
    {

        /*
        let a : int := mut 10
        let a_type : type[int?] := a.type
        let a_type_text := a_type.as_text
         */
        val vars8 = getVars("""{
  "parsed" : true,
  "components" : [ {
    "components" : [ {
      "name" : "INT",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "!nothing",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "name" : "#10",
        "type" : "TOKEN"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  }, {
    "components" : [ {
      "name" : "TYPE_TYPE",
      "components" : [ {
        "name" : "EITHER",
        "components" : [ {
          "name" : "INT",
          "type" : "TYPE"
        }, {
          "name" : "!nothing",
          "type" : "TYPE"
        } ],
        "type" : "TYPE"
      } ],
      "type" : "TYPE"
    }, {
      "name" : "!a_type",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "!type",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "components" : [ {
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "components" : [ {
              "name" : "!a",
              "type" : "TOKEN"
            } ],
            "type" : "EXPRESSION"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "METHOD_CALL"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  }, {
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a_type_text",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "!as_text",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "components" : [ {
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "components" : [ {
              "name" : "!a_type",
              "type" : "TOKEN"
            } ],
            "type" : "EXPRESSION"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "METHOD_CALL"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  } ],
  "type" : "CODE_BLOCK"
}""")
        assertVar(vars8, "!a_type_text","TYPE MUTABLE; (TYPE INT)")

        /*
        let a := "hello"
        let b := a.as_list
        let var c := ""
        for elem in b do
            c := c + elem + ", "
         */
        val vars6 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@hello",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "LIST",
      "type" : "TYPE",
      "components" : [ {
        "name" : "STRING",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!b",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!as_list",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!a",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!c",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "FOR",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!elem",
      "type" : "TOKEN"
    }, {
      "name" : "IN",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!b",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!c",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!c",
              "type" : "TOKEN"
            }, {
              "name" : "STRING|PLUS|STRING",
              "type" : "TOKEN"
            }, {
              "name" : "!elem",
              "type" : "TOKEN"
            } ]
          }, {
            "name" : "STRING|PLUS|STRING",
            "type" : "TOKEN"
          }, {
            "name" : "@, ",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars6, "!c","h, e, l, l, o, ")

        /*
        let a := 10 * "hi! "
        let b := "hi! " * 10
        let c := a = b
         */
        val vars5 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#10",
        "type" : "TOKEN"
      }, {
        "name" : "INT|TIMES|STRING",
        "type" : "TOKEN"
      }, {
        "name" : "@hi! ",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!b",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@hi! ",
        "type" : "TOKEN"
      }, {
        "name" : "STRING|TIMES|INT",
        "type" : "TOKEN"
      }, {
        "name" : "#10",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "BOOLEAN",
      "type" : "TYPE"
    }, {
      "name" : "!c",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN"
      }, {
        "name" : "EQUALS",
        "type" : "TOKEN"
      }, {
        "name" : "!b",
        "type" : "TOKEN"
      } ]
    } ]
  } ]
}""")
        assertVar(vars5, "!a", "hi! hi! hi! hi! hi! hi! hi! hi! hi! hi! ")
        assertVar(vars5, "!b", "hi! hi! hi! hi! hi! hi! hi! hi! hi! hi! ")
        assertVar(vars5, "!c", true)

        //let a := modulo.as_text
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!as_text",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!modulo",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars, "!a", "METHOD(TYPE INT; DECLARATION ANON_ARG; (TYPE INT; !x); DECLARATION ANON_ARG; (TYPE INT; !y))")

        // let result := 10
        val vars1 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
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
        assertVar(vars1, "!result", BigInteger.valueOf(10L))

        /*
        let a := mut 10
        let b := a
        a.set(20)
        */

        //assertVar(vars2,"!a",20L)
        //assertVar(vars2,"!b",20L)

        //let a := 2.0 ^ -1
        val vars3 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "DECIMAL",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2.0",
        "type" : "TOKEN"
      }, {
        "name" : "DECIMAL|POWER|INT",
        "type" : "TOKEN"
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!nothing",
          "type" : "TOKEN"
        }, {
          "name" : "!nothing|UNARY_MINUS|INT",
          "type" : "TOKEN"
        }, {
          "name" : "#1",
          "type" : "TOKEN"
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars3,"!a", (0.5).toBigDecimal(mathContext = DECIMAL128))

        /*
        let var a := true
        a := a or false
        a := a and true
         */
        val var4 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "BOOLEAN",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!true",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN"
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN"
      }, {
        "name" : "OR",
        "type" : "TOKEN"
      }, {
        "name" : "!false",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN"
      } ]
    }, {
      "name" : "ASSIGN",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
        "type" : "TOKEN"
      }, {
        "name" : "AND",
        "type" : "TOKEN"
      }, {
        "name" : "!true",
        "type" : "TOKEN"
      } ]
    } ]
  } ]
}""")
        assertVar(var4,"!a",true)

        // let a := 2.as_text
        val var5 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!as_text",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(var5,"!a","2")

        /*
        let a := mut (false or true)
        let b := mut a
        b.set(false)
         */
        //assertVar(var6,"!a",true)
        //assertVar(var6,"!b",false)
    }

    @Test
    fun ifTests()
    {
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    } ]
  }, {
    "type" : "IF",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#1",
        "type" : "TOKEN"
      }, {
        "name" : "INT|LARGER|INT",
        "type" : "TOKEN"
      }, {
        "name" : "#10",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "@branch 1",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!nothing",
        "type" : "TOKEN"
      }, {
        "name" : "!nothing|NOT|BOOLEAN",
        "type" : "TOKEN"
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#10",
          "type" : "TOKEN"
        }, {
          "name" : "INT|LARGER_EQUALS|INT",
          "type" : "TOKEN"
        }, {
          "name" : "#10",
          "type" : "TOKEN"
        } ]
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "@branch 2",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    }, {
      "name" : "ELSE",
      "type" : "TOKEN"
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#2",
              "type" : "TOKEN"
            }, {
              "name" : "INT|PLUS|INT",
              "type" : "TOKEN"
            }, {
              "name" : "#4",
              "type" : "TOKEN"
            } ]
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#6",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!a",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "@branch 3",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        }, {
          "name" : "ELSE",
          "type" : "TOKEN"
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!a",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "@branch 4",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!write_line",
      "type" : "TOKEN"
    }, {
      "name" : "METHOD|APPLY|METHOD_CALL",
      "type" : "TOKEN"
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!text",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars,"!a","branch 3")
    }

    @Test
    fun forTests()
    {
        /*
        let var reached12 := false
        let var reached13 := false
        let var reached21 := false
        for i from 1 to 100
        begin
            if i = 12 do skip
            if i = 12 do reached12 := true
            if i = 13 do reached13 := true
            if i = 20 do stop
            if i = 21 do reached21 := true
            write_line("" + i)
        end
         */
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "BOOLEAN",
      "type" : "TYPE"
    }, {
      "name" : "!reached12",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!false",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "BOOLEAN",
      "type" : "TYPE"
    }, {
      "name" : "!reached13",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!false",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "BOOLEAN",
      "type" : "TYPE"
    }, {
      "name" : "!reached21",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!false",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "FOR",
    "components" : [ {
      "name" : "INT",
      "type" : "TYPE"
    }, {
      "name" : "!i",
      "type" : "TOKEN"
    }, {
      "name" : "FROM",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#1",
        "type" : "TOKEN"
      } ]
    }, {
      "name" : "TO",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#100",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#12",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "CONTINUE"
          } ]
        } ]
      }, {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#12",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!reached12",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!true",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      }, {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#13",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!reached13",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!true",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      }, {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#20",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "BREAK"
          } ]
        } ]
      }, {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#21",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!reached21",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!true",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!write_line",
          "type" : "TOKEN"
        }, {
          "name" : "METHOD|APPLY|METHOD_CALL",
          "type" : "TOKEN"
        }, {
          "type" : "METHOD_CALL",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!text",
              "type" : "TOKEN"
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "@",
                "type" : "TOKEN"
              }, {
                "name" : "STRING|PLUS|INT",
                "type" : "TOKEN"
              }, {
                "name" : "!i",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars, "!reached12", false)
        assertVar(vars, "!reached13", true)
        assertVar(vars, "!reached21", false)
        assertVar(vars, "!i", BigInteger.valueOf(20L))
    }

    @Test
    fun whileTests()
    {
        /*
        let var a := ""
        let var i := 10
        while i>0
        begin
            i := i - 1
            if i = 5 do skip
            if i = 2 do stop
            a := a + i.as_text
        end
        write(a)
         */
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "@",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "name" : "VARIABLE",
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
      "type" : "TYPE"
    }, {
      "name" : "!i",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#10",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "WHILE",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!i",
        "type" : "TOKEN"
      }, {
        "name" : "INT|LARGER|INT",
        "type" : "TOKEN"
      }, {
        "name" : "#0",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "INT|MINUS|INT",
            "type" : "TOKEN"
          }, {
            "name" : "#1",
            "type" : "TOKEN"
          } ]
        } ]
      }, {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#5",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "CONTINUE"
          } ]
        } ]
      }, {
        "type" : "IF",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!i",
            "type" : "TOKEN"
          }, {
            "name" : "EQUALS",
            "type" : "TOKEN"
          }, {
            "name" : "#2",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "BREAK"
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          }, {
            "name" : "STRING|PLUS|STRING",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!as_text",
              "type" : "TOKEN"
            }, {
              "name" : "METHOD|APPLY|METHOD_CALL",
              "type" : "TOKEN"
            }, {
              "type" : "METHOD_CALL",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "!elem",
                  "type" : "TOKEN"
                }, {
                  "name" : "ASSIGN",
                  "type" : "TOKEN"
                }, {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "!i",
                    "type" : "TOKEN"
                  } ]
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!write",
      "type" : "TOKEN"
    }, {
      "name" : "METHOD|APPLY|METHOD_CALL",
      "type" : "TOKEN"
    }, {
      "type" : "METHOD_CALL",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!text",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars,"!a","987643")
        assertVar(vars,"!i",BigInteger.valueOf(2L))


        /*
        let var a := ""
        let var i := 10
        while i>0
        begin
            i := i - 1
            if modulo(i, 3) = 0 do skip
            a := a + i.as_text
        end
         */
        val vars2 = getVars("""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "name": "VARIABLE",
      "type": "DECLARATION",
      "components": [
        {
          "name": "STRING",
          "type": "TYPE"
        },
        {
          "name": "!a",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "@",
              "type": "TOKEN"
            }
          ]
        }
      ]
    },
    {
      "name": "VARIABLE",
      "type": "DECLARATION",
      "components": [
        {
          "name": "INT",
          "type": "TYPE"
        },
        {
          "name": "!i",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "#10",
              "type": "TOKEN"
            }
          ]
        }
      ]
    },
    {
      "type": "WHILE",
      "components": [
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!i",
              "type": "TOKEN"
            },
            {
              "name": "INT|LARGER|INT",
              "type": "TOKEN"
            },
            {
              "name": "#0",
              "type": "TOKEN"
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
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!i",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "name": "ASSIGN",
                  "type": "TOKEN"
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!i",
                      "type": "TOKEN"
                    },
                    {
                      "name": "INT|MINUS|INT",
                      "type": "TOKEN"
                    },
                    {
                      "name": "#1",
                      "type": "TOKEN"
                    }
                  ]
                }
              ]
            },
            {
              "type": "IF",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!modulo",
                          "type": "TOKEN"
                        },
                        {
                          "name": "METHOD|APPLY|METHOD_CALL",
                          "type": "TOKEN"
                        },
                        {
                          "type": "METHOD_CALL",
                          "components": [
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!x",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!i",
                                      "type": "TOKEN"
                                    }
                                  ]
                                }
                              ]
                            },
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!y",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "#3",
                                      "type": "TOKEN"
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
                      "name": "EQUALS",
                      "type": "TOKEN"
                    },
                    {
                      "name": "#0",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "CONTINUE"
                    }
                  ]
                }
              ]
            },
            {
              "type": "EXPRESSION",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!a",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "name": "ASSIGN",
                  "type": "TOKEN"
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!a",
                      "type": "TOKEN"
                    },
                    {
                      "name": "STRING|PLUS|STRING",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!as_text",
                          "type": "TOKEN"
                        },
                        {
                          "name": "METHOD|APPLY|METHOD_CALL",
                          "type": "TOKEN"
                        },
                        {
                          "type": "METHOD_CALL",
                          "components": [
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!elem",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!i",
                                      "type": "TOKEN"
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
}""")
        assertVar(vars2, "!a", "875421")
    }

    @Test
    fun listTests()
    {
        val vars = getVars("""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "LIST",
          "type": "TYPE",
          "components": [
            {
              "name": "INT",
              "type": "TYPE"
            }
          ]
        },
        {
          "name": "!list",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "LIST",
              "components": [
                {
                  "name": "INT",
                  "type": "TYPE"
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!nothing",
                      "type": "TOKEN"
                    },
                    {
                      "name": "MUTABLE",
                      "type": "TOKEN"
                    },
                    {
                      "name": "#2",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#3",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#1",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#0",
                      "type": "TOKEN"
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
      "name": "VARIABLE",
      "type": "DECLARATION",
      "components": [
        {
          "name": "STRING",
          "type": "TYPE"
        },
        {
          "name": "!text1",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "@",
              "type": "TOKEN"
            }
          ]
        }
      ]
    },
    {
      "name": "VARIABLE",
      "type": "DECLARATION",
      "components": [
        {
          "name": "STRING",
          "type": "TYPE"
        },
        {
          "name": "!text2",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "@",
              "type": "TOKEN"
            }
          ]
        }
      ]
    },
    {
      "type": "FOR",
      "components": [
        {
          "name": "INT",
          "type": "TYPE"
        },
        {
          "name": "!elem",
          "type": "TOKEN"
        },
        {
          "name": "IN",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!list",
              "type": "TOKEN"
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
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!text1",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "name": "ASSIGN",
                  "type": "TOKEN"
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!text1",
                      "type": "TOKEN"
                    },
                    {
                      "name": "STRING|PLUS|STRING",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!as_text",
                          "type": "TOKEN"
                        },
                        {
                          "name": "METHOD|APPLY|METHOD_CALL",
                          "type": "TOKEN"
                        },
                        {
                          "type": "METHOD_CALL",
                          "components": [
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!elem",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!elem",
                                      "type": "TOKEN"
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
              "type": "EXPRESSION",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!text2",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "name": "ASSIGN",
                  "type": "TOKEN"
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!text2",
                      "type": "TOKEN"
                    },
                    {
                      "name": "STRING|PLUS|STRING",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!as_text",
                          "type": "TOKEN"
                        },
                        {
                          "name": "METHOD|APPLY|METHOD_CALL",
                          "type": "TOKEN"
                        },
                        {
                          "type": "METHOD_CALL",
                          "components": [
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!elem",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!get",
                                      "type": "TOKEN"
                                    },
                                    {
                                      "name": "METHOD|APPLY|METHOD_CALL",
                                      "type": "TOKEN"
                                    },
                                    {
                                      "type": "METHOD_CALL",
                                      "components": [
                                        {
                                          "type": "EXPRESSION",
                                          "components": [
                                            {
                                              "name": "!at",
                                              "type": "TOKEN"
                                            },
                                            {
                                              "name": "ASSIGN",
                                              "type": "TOKEN"
                                            },
                                            {
                                              "type": "EXPRESSION",
                                              "components": [
                                                {
                                                  "name": "!elem",
                                                  "type": "TOKEN"
                                                }
                                              ]
                                            }
                                          ]
                                        },
                                        {
                                          "type": "EXPRESSION",
                                          "components": [
                                            {
                                              "name": "!collection",
                                              "type": "TOKEN"
                                            },
                                            {
                                              "name": "ASSIGN",
                                              "type": "TOKEN"
                                            },
                                            {
                                              "type": "EXPRESSION",
                                              "components": [
                                                {
                                                  "name": "!list",
                                                  "type": "TOKEN"
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
        }
      ]
    }
  ]
}""")
        assertVar(vars, "!text1", "2310")
        assertVar(vars, "!text2", "1032")

        /*
        let a := mut [1, "hi", nothing] : anything?
        let b := (a.get(2)).as_text
        b.write_line
         */
        val vars2 = getVars("""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "MUTABLE",
          "type": "TYPE",
          "components": [
            {
              "name": "LIST",
              "type": "TYPE",
              "components": [
                {
                  "name": "EITHER",
                  "type": "TYPE",
                  "components": [
                    {
                      "name": "ANYTHING",
                      "type": "TYPE"
                    },
                    {
                      "name": "!nothing",
                      "type": "TYPE"
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          "name": "!a",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!nothing",
              "type": "TOKEN"
            },
            {
              "name": "MUTABLE",
              "type": "TOKEN"
            },
            {
              "type": "LIST",
              "components": [
                {
                  "name": "EITHER",
                  "type": "TYPE",
                  "components": [
                    {
                      "name": "ANYTHING",
                      "type": "TYPE"
                    },
                    {
                      "name": "!nothing",
                      "type": "TYPE"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "#1",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "@hi",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!nothing",
                      "type": "TOKEN"
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
          "name": "STRING",
          "type": "TYPE"
        },
        {
          "name": "!b",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!as_text",
              "type": "TOKEN"
            },
            {
              "name": "METHOD|APPLY|METHOD_CALL",
              "type": "TOKEN"
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!elem",
                      "type": "TOKEN"
                    },
                    {
                      "name": "ASSIGN",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!get",
                          "type": "TOKEN"
                        },
                        {
                          "name": "METHOD|APPLY|METHOD_CALL",
                          "type": "TOKEN"
                        },
                        {
                          "type": "METHOD_CALL",
                          "components": [
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!at",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "#2",
                                      "type": "TOKEN"
                                    }
                                  ]
                                }
                              ]
                            },
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!collection",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!a",
                                      "type": "TOKEN"
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
      "type": "EXPRESSION",
      "components": [
        {
          "name": "!write_line",
          "type": "TOKEN"
        },
        {
          "name": "METHOD|APPLY|METHOD_CALL",
          "type": "TOKEN"
        },
        {
          "type": "METHOD_CALL",
          "components": [
            {
              "type": "EXPRESSION",
              "components": [
                {
                  "name": "!text",
                  "type": "TOKEN"
                },
                {
                  "name": "ASSIGN",
                  "type": "TOKEN"
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!b",
                      "type": "TOKEN"
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
}""")
        assertVar(vars2, "!b", "nothing")

        /*
        let list := mut [1,2,3]
        let list2 := new list
        list2.set([1,2,3])
        let list_text := list2.as_text
         */
            //assertVar(vars4, "!list_text", "[1, 2, 3]")
    }

    @Test
    fun whenTests()
    {
        /*
        let list : mut[list[mut[int?]]] := mut [mut 1, mut nothing] : mut[int?]
        let var text := ""
        for i from 0 to 3
        begin
            let x : int?
            if begin
                i < list.size do x := list.get(i)
                default do x := nothing
            end
            when x begin
                is mut[nothing] do text := text + "mut[nothing],"
                is nothing do text := text + "nothing,"
                default do text := text + "default,"
            end
        end
         */
        val vars = getVars("""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "MUTABLE",
          "type": "TYPE",
          "components": [
            {
              "name": "LIST",
              "type": "TYPE",
              "components": [
                {
                  "name": "MUTABLE",
                  "type": "TYPE",
                  "components": [
                    {
                      "name": "EITHER",
                      "type": "TYPE",
                      "components": [
                        {
                          "name": "INT",
                          "type": "TYPE"
                        },
                        {
                          "name": "!nothing",
                          "type": "TYPE"
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
          "name": "!list",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!nothing",
              "type": "TOKEN"
            },
            {
              "name": "MUTABLE",
              "type": "TOKEN"
            },
            {
              "type": "LIST",
              "components": [
                {
                  "name": "MUTABLE",
                  "type": "TYPE",
                  "components": [
                    {
                      "name": "EITHER",
                      "type": "TYPE",
                      "components": [
                        {
                          "name": "INT",
                          "type": "TYPE"
                        },
                        {
                          "name": "!nothing",
                          "type": "TYPE"
                        }
                      ]
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!nothing",
                      "type": "TOKEN"
                    },
                    {
                      "name": "MUTABLE",
                      "type": "TOKEN"
                    },
                    {
                      "name": "#1",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!nothing",
                      "type": "TOKEN"
                    },
                    {
                      "name": "MUTABLE",
                      "type": "TOKEN"
                    },
                    {
                      "name": "!nothing",
                      "type": "TOKEN"
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
      "name": "VARIABLE",
      "type": "DECLARATION",
      "components": [
        {
          "name": "STRING",
          "type": "TYPE"
        },
        {
          "name": "!text",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "@",
              "type": "TOKEN"
            }
          ]
        }
      ]
    },
    {
      "type": "FOR",
      "components": [
        {
          "name": "INT",
          "type": "TYPE"
        },
        {
          "name": "!i",
          "type": "TOKEN"
        },
        {
          "name": "FROM",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "#0",
              "type": "TOKEN"
            }
          ]
        },
        {
          "name": "TO",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "#3",
              "type": "TOKEN"
            }
          ]
        },
        {
          "type": "CODE_BLOCK",
          "components": [
            {
              "type": "DECLARATION",
              "components": [
                {
                  "name": "EITHER",
                  "type": "TYPE",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE"
                    },
                    {
                      "name": "!nothing",
                      "type": "TYPE"
                    }
                  ]
                },
                {
                  "name": "!x",
                  "type": "TOKEN"
                }
              ]
            },
            {
              "type": "IF",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!i",
                      "type": "TOKEN"
                    },
                    {
                      "name": "INT|SMALLER|INT",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "!size",
                          "type": "TOKEN"
                        },
                        {
                          "name": "METHOD|APPLY|METHOD_CALL",
                          "type": "TOKEN"
                        },
                        {
                          "type": "METHOD_CALL",
                          "components": [
                            {
                              "type": "EXPRESSION",
                              "components": [
                                {
                                  "name": "!elem",
                                  "type": "TOKEN"
                                },
                                {
                                  "name": "ASSIGN",
                                  "type": "TOKEN"
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!list",
                                      "type": "TOKEN"
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
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!x",
                              "type": "TOKEN"
                            }
                          ]
                        },
                        {
                          "name": "ASSIGN",
                          "type": "TOKEN"
                        },
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!get",
                              "type": "TOKEN"
                            },
                            {
                              "name": "METHOD|APPLY|METHOD_CALL",
                              "type": "TOKEN"
                            },
                            {
                              "type": "METHOD_CALL",
                              "components": [
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!at",
                                      "type": "TOKEN"
                                    },
                                    {
                                      "name": "ASSIGN",
                                      "type": "TOKEN"
                                    },
                                    {
                                      "type": "EXPRESSION",
                                      "components": [
                                        {
                                          "name": "!i",
                                          "type": "TOKEN"
                                        }
                                      ]
                                    }
                                  ]
                                },
                                {
                                  "type": "EXPRESSION",
                                  "components": [
                                    {
                                      "name": "!collection",
                                      "type": "TOKEN"
                                    },
                                    {
                                      "name": "ASSIGN",
                                      "type": "TOKEN"
                                    },
                                    {
                                      "type": "EXPRESSION",
                                      "components": [
                                        {
                                          "name": "!list",
                                          "type": "TOKEN"
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
                  "name": "ELSE",
                  "type": "TOKEN"
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!x",
                              "type": "TOKEN"
                            }
                          ]
                        },
                        {
                          "name": "ASSIGN",
                          "type": "TOKEN"
                        },
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!nothing",
                              "type": "TOKEN"
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
              "type": "WHEN",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!x",
                      "type": "TOKEN"
                    }
                  ]
                },
                {
                  "name": "MUTABLE",
                  "type": "TYPE",
                  "components": [
                    {
                      "name": "!nothing",
                      "type": "TYPE"
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
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!text",
                              "type": "TOKEN"
                            }
                          ]
                        },
                        {
                          "name": "ASSIGN",
                          "type": "TOKEN"
                        },
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!text",
                              "type": "TOKEN"
                            },
                            {
                              "name": "STRING|PLUS|STRING",
                              "type": "TOKEN"
                            },
                            {
                              "name": "@mut[nothing],",
                              "type": "TOKEN"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                },
                {
                  "name": "!nothing",
                  "type": "TYPE"
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!text",
                              "type": "TOKEN"
                            }
                          ]
                        },
                        {
                          "name": "ASSIGN",
                          "type": "TOKEN"
                        },
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!text",
                              "type": "TOKEN"
                            },
                            {
                              "name": "STRING|PLUS|STRING",
                              "type": "TOKEN"
                            },
                            {
                              "name": "@nothing,",
                              "type": "TOKEN"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                },
                {
                  "name": "ELSE",
                  "type": "TYPE"
                },
                {
                  "type": "CODE_BLOCK",
                  "components": [
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!text",
                              "type": "TOKEN"
                            }
                          ]
                        },
                        {
                          "name": "ASSIGN",
                          "type": "TOKEN"
                        },
                        {
                          "type": "EXPRESSION",
                          "components": [
                            {
                              "name": "!text",
                              "type": "TOKEN"
                            },
                            {
                              "name": "STRING|PLUS|STRING",
                              "type": "TOKEN"
                            },
                            {
                              "name": "@default,",
                              "type": "TOKEN"
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
}""")
        assertVar(vars, "!text", "default,mut[nothing],nothing,")
    }

    @Test
    fun methodLiteralTest()
    {
        /*
        let func := fun(a := 1, b : int) do yield a + b
        let a := func(a := 10, b := 20)
        let b := func(b := 20)
         */
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "METHOD",
      "type" : "TYPE",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "INT",
          "type" : "TYPE"
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT",
            "type" : "TYPE"
          }, {
            "name" : "!a",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#1",
              "type" : "TOKEN"
            } ]
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT",
            "type" : "TYPE"
          }, {
            "name" : "!b",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    }, {
      "name" : "!func",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "METHOD",
        "components" : [ {
          "name" : "INT",
          "type" : "TYPE"
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT",
            "type" : "TYPE"
          }, {
            "name" : "!a",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#1",
              "type" : "TOKEN"
            } ]
          } ]
        }, {
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT",
            "type" : "TYPE"
          }, {
            "name" : "!b",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "RETURN",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!a",
                "type" : "TOKEN"
              }, {
                "name" : "INT|PLUS|INT",
                "type" : "TOKEN"
              }, {
                "name" : "!b",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!func",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!a",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#10",
              "type" : "TOKEN"
            } ]
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#20",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "INT",
      "type" : "TYPE"
    }, {
      "name" : "!b",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!func",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "type" : "METHOD_CALL",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "#20",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars, "!a", BigInteger.valueOf(30L))
        assertVar(vars, "!b", BigInteger.valueOf(21L))

        /*
        let create_addition := fun(arg x : int) do
            yield fun(arg y : int) do
                yield import x + y
        let add5 := create_addition(5)
        let add10 := create_addition(10)
        let x := add5(10)
        let y := add10(10)
         */
        val vars2 = getVars("""{
  "parsed": true,
  "type": "CODE_BLOCK",
  "components": [
    {
      "type": "DECLARATION",
      "components": [
        {
          "name": "METHOD",
          "type": "TYPE",
          "components": [
            {
              "type": "METHOD",
              "components": [
                {
                  "name": "METHOD",
                  "type": "TYPE",
                  "components": [
                    {
                      "type": "METHOD",
                      "components": [
                        {
                          "name": "INT",
                          "type": "TYPE"
                        },
                        {
                          "name": "ANON_ARG",
                          "type": "DECLARATION",
                          "components": [
                            {
                              "name": "INT",
                              "type": "TYPE"
                            },
                            {
                              "name": "!y",
                              "type": "TOKEN"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                },
                {
                  "name": "ANON_ARG",
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE"
                    },
                    {
                      "name": "!x",
                      "type": "TOKEN"
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          "name": "!create_addition",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "type": "METHOD",
              "components": [
                {
                  "name": "METHOD",
                  "type": "TYPE",
                  "components": [
                    {
                      "type": "METHOD",
                      "components": [
                        {
                          "name": "INT",
                          "type": "TYPE"
                        },
                        {
                          "name": "ANON_ARG",
                          "type": "DECLARATION",
                          "components": [
                            {
                              "name": "INT",
                              "type": "TYPE"
                            },
                            {
                              "name": "!y",
                              "type": "TOKEN"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                },
                {
                  "name": "ANON_ARG",
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE"
                    },
                    {
                      "name": "!x",
                      "type": "TOKEN"
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
                              "components": [
                                {
                                  "name": "INT",
                                  "type": "TYPE"
                                },
                                {
                                  "name": "ANON_ARG",
                                  "type": "DECLARATION",
                                  "components": [
                                    {
                                      "name": "INT",
                                      "type": "TYPE"
                                    },
                                    {
                                      "name": "!y",
                                      "type": "TOKEN"
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
                                              "name": "!x",
                                              "type": "TOKEN"
                                            },
                                            {
                                              "name": "INT|PLUS|INT",
                                              "type": "TOKEN"
                                            },
                                            {
                                              "name": "!y",
                                              "type": "TOKEN"
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
          "name": "METHOD",
          "type": "TYPE",
          "components": [
            {
              "type": "METHOD",
              "components": [
                {
                  "name": "INT",
                  "type": "TYPE"
                },
                {
                  "name": "ANON_ARG",
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE"
                    },
                    {
                      "name": "!y",
                      "type": "TOKEN"
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          "name": "!add5",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!create_addition",
              "type": "TOKEN"
            },
            {
              "name": "METHOD|APPLY|METHOD_CALL",
              "type": "TOKEN"
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!x",
                      "type": "TOKEN"
                    },
                    {
                      "name": "ASSIGN",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "#5",
                          "type": "TOKEN"
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
          "name": "METHOD",
          "type": "TYPE",
          "components": [
            {
              "type": "METHOD",
              "components": [
                {
                  "name": "INT",
                  "type": "TYPE"
                },
                {
                  "name": "ANON_ARG",
                  "type": "DECLARATION",
                  "components": [
                    {
                      "name": "INT",
                      "type": "TYPE"
                    },
                    {
                      "name": "!y",
                      "type": "TOKEN"
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          "name": "!add10",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!create_addition",
              "type": "TOKEN"
            },
            {
              "name": "METHOD|APPLY|METHOD_CALL",
              "type": "TOKEN"
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!x",
                      "type": "TOKEN"
                    },
                    {
                      "name": "ASSIGN",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "#10",
                          "type": "TOKEN"
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
          "name": "INT",
          "type": "TYPE"
        },
        {
          "name": "!x",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!add5",
              "type": "TOKEN"
            },
            {
              "name": "METHOD|APPLY|METHOD_CALL",
              "type": "TOKEN"
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!y",
                      "type": "TOKEN"
                    },
                    {
                      "name": "ASSIGN",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "#10",
                          "type": "TOKEN"
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
          "name": "INT",
          "type": "TYPE"
        },
        {
          "name": "!y",
          "type": "TOKEN"
        },
        {
          "type": "EXPRESSION",
          "components": [
            {
              "name": "!add10",
              "type": "TOKEN"
            },
            {
              "name": "METHOD|APPLY|METHOD_CALL",
              "type": "TOKEN"
            },
            {
              "type": "METHOD_CALL",
              "components": [
                {
                  "type": "EXPRESSION",
                  "components": [
                    {
                      "name": "!y",
                      "type": "TOKEN"
                    },
                    {
                      "name": "ASSIGN",
                      "type": "TOKEN"
                    },
                    {
                      "type": "EXPRESSION",
                      "components": [
                        {
                          "name": "#10",
                          "type": "TOKEN"
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
}""")
        assertVar(vars2,"!x", BigInteger.valueOf(15L))
        assertVar(vars2,"!y", BigInteger.valueOf(20L))
    }

    @Test
    fun standardLibraryTests()
    {
        /*
        let a : int? := 10
        write_line(a.content)
         */

        val vars10 = getVars("""{
  "parsed" : true,
  "components" : [ {
    "components" : [ {
      "name" : "EITHER",
      "components" : [ {
        "name" : "INT",
        "type" : "TYPE"
      }, {
        "name" : "!nothing",
        "type" : "TYPE"
      } ],
      "type" : "TYPE"
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "#10",
        "type" : "TOKEN"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  }, {
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!text",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "!as_text",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "components" : [ {
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "components" : [ {
              "name" : "!content",
              "type" : "TOKEN"
            }, {
              "name" : "METHOD|APPLY|METHOD_CALL",
              "type" : "TOKEN"
            }, {
              "components" : [ {
                "components" : [ {
                  "name" : "!elem",
                  "type" : "TOKEN"
                }, {
                  "name" : "ASSIGN",
                  "type" : "TOKEN"
                }, {
                  "components" : [ {
                    "name" : "!a",
                    "type" : "TOKEN"
                  } ],
                  "type" : "EXPRESSION"
                } ],
                "type" : "EXPRESSION"
              } ],
              "type" : "METHOD_CALL"
            } ],
            "type" : "EXPRESSION"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "METHOD_CALL"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  } ],
  "type" : "CODE_BLOCK"
}""")
        assertVar(vars10,"!text","10")

        /*
        let list := mut [1, 2, 3]
        list.add(4)
        list.add(index := 3, 5)
        let list_text := list.as_text
         */
        val vars = getVars("""{
  "parsed" : true,
  "components" : [ {
    "components" : [ {
      "name" : "MUTABLE",
      "components" : [ {
        "name" : "LIST",
        "components" : [ {
          "name" : "INT",
          "type" : "TYPE"
        } ],
        "type" : "TYPE"
      } ],
      "type" : "TYPE"
    }, {
      "name" : "!list",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "!nothing",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "components" : [ {
          "name" : "INT",
          "type" : "TYPE"
        }, {
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        }, {
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        }, {
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "LIST"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  }, {
    "components" : [ {
      "name" : "!add",
      "type" : "TOKEN"
    }, {
      "name" : "METHOD|APPLY|METHOD_CALL",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "components" : [ {
          "name" : "!at",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "components" : [ {
            "name" : "!size",
            "type" : "TOKEN"
          }, {
            "name" : "METHOD|APPLY|METHOD_CALL",
            "type" : "TOKEN"
          }, {
            "components" : [ {
              "components" : [ {
                "name" : "!elem",
                "type" : "TOKEN"
              }, {
                "name" : "ASSIGN",
                "type" : "TOKEN"
              }, {
                "components" : [ {
                  "name" : "!list",
                  "type" : "TOKEN"
                } ],
                "type" : "EXPRESSION"
              } ],
              "type" : "EXPRESSION"
            } ],
            "type" : "METHOD_CALL"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "EXPRESSION"
      }, {
        "components" : [ {
          "name" : "!value",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "components" : [ {
            "name" : "#4",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "EXPRESSION"
      }, {
        "components" : [ {
          "name" : "!collection",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "components" : [ {
            "name" : "!list",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "EXPRESSION"
      } ],
      "type" : "METHOD_CALL"
    } ],
    "type" : "EXPRESSION"
  }, {
    "components" : [ {
      "name" : "!add",
      "type" : "TOKEN"
    }, {
      "name" : "METHOD|APPLY|METHOD_CALL",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "components" : [ {
          "name" : "!at",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "EXPRESSION"
      }, {
        "components" : [ {
          "name" : "!value",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "components" : [ {
            "name" : "#5",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "EXPRESSION"
      }, {
        "components" : [ {
          "name" : "!collection",
          "type" : "TOKEN"
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "components" : [ {
            "name" : "!list",
            "type" : "TOKEN"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "EXPRESSION"
      } ],
      "type" : "METHOD_CALL"
    } ],
    "type" : "EXPRESSION"
  }, {
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!list_text",
      "type" : "TOKEN"
    }, {
      "components" : [ {
        "name" : "!as_text",
        "type" : "TOKEN"
      }, {
        "name" : "METHOD|APPLY|METHOD_CALL",
        "type" : "TOKEN"
      }, {
        "components" : [ {
          "components" : [ {
            "name" : "!elem",
            "type" : "TOKEN"
          }, {
            "name" : "ASSIGN",
            "type" : "TOKEN"
          }, {
            "components" : [ {
              "name" : "!list",
              "type" : "TOKEN"
            } ],
            "type" : "EXPRESSION"
          } ],
          "type" : "EXPRESSION"
        } ],
        "type" : "METHOD_CALL"
      } ],
      "type" : "EXPRESSION"
    } ],
    "type" : "DECLARATION"
  } ],
  "type" : "CODE_BLOCK"
}""")
        assertVar(vars,"!list_text","[1, 2, 3, 5, 4]")
    }
}