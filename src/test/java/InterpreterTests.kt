
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import wiles.Main
import wiles.interpreter.Interpreter
import wiles.interpreter.data.VariableMap

class InterpreterTests {
    private fun assertVar(vars : VariableMap, name : String, value : Any?)
    {
        assert(vars[name]?.value == value)
    }

    private fun getVars(code : String) : VariableMap
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

        /*
        let a := mut 10
        let b := a
        a <- 20
        */
        val vars2 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "INT64",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "name" : "#10",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "INT64",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!b",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!a",
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
      "name" : "MODIFY",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#20",
        "type" : "TOKEN"
      } ]
    } ]
  } ]
}""")
        assertVar(vars2,"!a",20L)
        assertVar(vars2,"!b",20L)

        //let a := 2.0 ^ -1
        val vars3 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "DOUBLE",
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
        "name" : "DOUBLE|POWER|INT64",
        "type" : "TOKEN"
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "NOTHING",
          "type" : "TOKEN"
        }, {
          "name" : "NOTHING|UNARY_MINUS|INT64",
          "type" : "TOKEN"
        }, {
          "name" : "#1",
          "type" : "TOKEN"
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars3,"!a", 0.5)

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
        "name" : "TRUE",
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
        "name" : "FALSE",
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
        "name" : "TRUE",
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
        "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
        b <- false
         */
        val var6 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "BOOLEAN",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "FALSE",
          "type" : "TOKEN"
        }, {
          "name" : "OR",
          "type" : "TOKEN"
        }, {
          "name" : "TRUE",
          "type" : "TOKEN"
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "BOOLEAN",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!b",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "name" : "!a",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!b",
        "type" : "TOKEN"
      } ]
    }, {
      "name" : "MODIFY",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "FALSE",
        "type" : "TOKEN"
      } ]
    } ]
  } ]
}""")
        assertVar(var6,"!a",true)
        assertVar(var6,"!b",false)
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
        "name" : "INT64|LARGER|INT64",
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
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "NOTHING|NOT|BOOLEAN",
        "type" : "TOKEN"
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "#10",
          "type" : "TOKEN"
        }, {
          "name" : "INT64|LARGER_EQUALS|INT64",
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
              "name" : "INT64|PLUS|INT64",
              "type" : "TOKEN"
            }, {
              "name" : "#4",
              "type" : "TOKEN"
            } ]
          }, {
            "name" : "INT64|EQUALS|INT64",
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
      "name" : "!writeline",
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
            writeline("" + i)
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
        "name" : "FALSE",
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
        "name" : "FALSE",
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
        "name" : "FALSE",
        "type" : "TOKEN"
      } ]
    } ]
  }, {
    "type" : "FOR",
    "components" : [ {
      "name" : "INT64",
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
            "name" : "INT64|EQUALS|INT64",
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
            "name" : "INT64|EQUALS|INT64",
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
                "name" : "TRUE",
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
            "name" : "INT64|EQUALS|INT64",
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
                "name" : "TRUE",
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
            "name" : "INT64|EQUALS|INT64",
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
            "name" : "INT64|EQUALS|INT64",
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
                "name" : "TRUE",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "name" : "!writeline",
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
                "name" : "STRING|PLUS|INT64",
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
        assertVar(vars, "!i", 20L)
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
      "name" : "INT64",
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
        "name" : "INT64|LARGER|INT64",
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
            "name" : "INT64|MINUS|INT64",
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
            "name" : "INT64|EQUALS|INT64",
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
            "name" : "INT64|EQUALS|INT64",
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
              "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
        assertVar(vars,"!i",2L)


        /*
        let var a := ""
        let var i := 10
        while i>0
        begin
            i := i - 1
            if modulo(i, 3) = 0 do skip
            a := a + i.as_text
        end
        panic(a)
         */
        val vars2 = getVars("""{
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
      "name" : "INT64",
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
        "name" : "INT64|LARGER|INT64",
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
            "name" : "INT64|MINUS|INT64",
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
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!modulo",
              "type" : "TOKEN"
            }, {
              "name" : "METHOD|APPLY|METHOD_CALL",
              "type" : "TOKEN"
            }, {
              "type" : "METHOD_CALL",
              "components" : [ {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "!x",
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
              }, {
                "type" : "EXPRESSION",
                "components" : [ {
                  "name" : "!y",
                  "type" : "TOKEN"
                }, {
                  "name" : "ASSIGN",
                  "type" : "TOKEN"
                }, {
                  "type" : "EXPRESSION",
                  "components" : [ {
                    "name" : "#3",
                    "type" : "TOKEN"
                  } ]
                } ]
              } ]
            } ]
          }, {
            "name" : "INT64|EQUALS|INT64",
            "type" : "TOKEN"
          }, {
            "name" : "#0",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "CONTINUE"
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
              "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
      "name" : "!panic",
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
        assertVar(vars2, "!a", "875421")
    }

    @Test
    fun listTests()
    {
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "LIST",
      "type" : "TYPE",
      "components" : [ {
        "name" : "INT64",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!list",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "type" : "LIST",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN"
          }, {
            "name" : "MUTABLE",
            "type" : "TOKEN"
          }, {
            "name" : "#2",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN"
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
      "name" : "!text1",
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
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!text2",
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
      "name" : "INT64",
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
        "name" : "!list",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!text1",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!text1",
            "type" : "TOKEN"
          }, {
            "name" : "STRING|PLUS|STRING",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
                    "name" : "!elem",
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
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!text2",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!text2",
            "type" : "TOKEN"
          }, {
            "name" : "STRING|PLUS|STRING",
            "type" : "TOKEN"
          }, {
            "type" : "EXPRESSION",
            "components" : [ {
              "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
                    "name" : "!list",
                    "type" : "TOKEN"
                  }, {
                    "name" : "ELEM_ACCESS",
                    "type" : "TOKEN"
                  }, {
                    "name" : "!elem",
                    "type" : "TOKEN"
                  } ]
                } ]
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars, "!text1", "231")
        assertVar(vars, "!text2", "1nothing3")

        /*
        let a := mut [1, "hi", nothing] : anything?
        let b := (a @ 2).as_text
        b.writeline
         */
        val vars2 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "LIST",
        "type" : "TYPE",
        "components" : [ {
          "name" : "MUTABLE",
          "type" : "TYPE",
          "components" : [ {
            "name" : "EITHER",
            "type" : "TYPE",
            "components" : [ {
              "name" : "ANYTHING",
              "type" : "TYPE"
            }, {
              "name" : "NOTHING",
              "type" : "TYPE"
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "type" : "LIST",
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "components" : [ {
            "name" : "ANYTHING",
            "type" : "TYPE"
          }, {
            "name" : "NOTHING",
            "type" : "TYPE"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "@hi",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN"
          } ]
        } ]
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
        "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
            }, {
              "name" : "ELEM_ACCESS",
              "type" : "TOKEN"
            }, {
              "name" : "#2",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
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
            "name" : "!b",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars2, "!b", "nothing")

        /*
        let list := mut [1, 2, 3]
        let var b := ""
        for elem in list
        begin
            list <- mut [4, 5, 6]
            b := b + elem
        end
        writeline(b)
         */
        val vars3 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "LIST",
        "type" : "TYPE",
        "components" : [ {
          "name" : "MUTABLE",
          "type" : "TYPE",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE"
          } ]
        } ]
      } ]
    }, {
      "name" : "!list",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "type" : "LIST",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN"
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
      "name" : "!b",
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
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "INT64",
        "type" : "TYPE"
      } ]
    }, {
      "name" : "!elem",
      "type" : "TOKEN"
    }, {
      "name" : "IN",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!list",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!list",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "MODIFY",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN"
          }, {
            "name" : "MUTABLE",
            "type" : "TOKEN"
          }, {
            "type" : "LIST",
            "components" : [ {
              "name" : "INT64",
              "type" : "TYPE"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#4",
                "type" : "TOKEN"
              } ]
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#5",
                "type" : "TOKEN"
              } ]
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "#6",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      }, {
        "type" : "EXPRESSION",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "ASSIGN",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!b",
            "type" : "TOKEN"
          }, {
            "name" : "STRING|PLUS|INT64",
            "type" : "TOKEN"
          }, {
            "name" : "!elem",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
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
            "name" : "!b",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars3, "!b", "123")

        val vars4 = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "LIST",
        "type" : "TYPE",
        "components" : [ {
          "name" : "MUTABLE",
          "type" : "TYPE",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE"
          } ]
        } ]
      } ]
    }, {
      "name" : "!a",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "type" : "LIST",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#2",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#3",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "LIST",
      "type" : "TYPE",
      "components" : [ {
        "name" : "MUTABLE",
        "type" : "TYPE",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE"
        } ]
      } ]
    }, {
      "name" : "!b",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "NEW",
        "type" : "TOKEN"
      }, {
        "name" : "!a",
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
      "name" : "MODIFY",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "type" : "LIST",
        "components" : [ {
          "name" : "INT64",
          "type" : "TYPE"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#4",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#5",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#6",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "STRING",
      "type" : "TYPE"
    }, {
      "name" : "!b_text",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "!TYPE EITHER; (TYPE ANYTHING; TYPE NOTHING)!as_text",
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
              "name" : "!b",
              "type" : "TOKEN"
            } ]
          } ]
        } ]
      } ]
    } ]
  }, {
    "type" : "EXPRESSION",
    "components" : [ {
      "name" : "!writeline",
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
            "name" : "!b_text",
            "type" : "TOKEN"
          } ]
        } ]
      } ]
    } ]
  } ]
}""")
        assertVar(vars4, "!b_text", "[1, 2, 3]")
    }

    @Test
    fun whenTests()
    {
        /*
        let list := mut [1, nothing] : int?
        let var text := ""
        for i from 0 to 2
        begin
            let x := list @ i
            when x is begin
                mut[nothing] do text := text + "mut[nothing],"
                nothing do text := text + "nothing,"
                default do text := text + "default,"
            end
        end
         */
        val vars = getVars("""{
  "type" : "CODE_BLOCK",
  "parsed" : true,
  "components" : [ {
    "type" : "DECLARATION",
    "components" : [ {
      "name" : "MUTABLE",
      "type" : "TYPE",
      "components" : [ {
        "name" : "LIST",
        "type" : "TYPE",
        "components" : [ {
          "name" : "MUTABLE",
          "type" : "TYPE",
          "components" : [ {
            "name" : "EITHER",
            "type" : "TYPE",
            "components" : [ {
              "name" : "INT64",
              "type" : "TYPE"
            }, {
              "name" : "NOTHING",
              "type" : "TYPE"
            } ]
          } ]
        } ]
      } ]
    }, {
      "name" : "!list",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "NOTHING",
        "type" : "TOKEN"
      }, {
        "name" : "MUTABLE",
        "type" : "TOKEN"
      }, {
        "type" : "LIST",
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "components" : [ {
            "name" : "INT64",
            "type" : "TYPE"
          }, {
            "name" : "NOTHING",
            "type" : "TYPE"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "#1",
            "type" : "TOKEN"
          } ]
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TOKEN"
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
      "name" : "!text",
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
      "name" : "INT64",
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
        "name" : "#0",
        "type" : "TOKEN"
      } ]
    }, {
      "name" : "TO",
      "type" : "TOKEN"
    }, {
      "type" : "EXPRESSION",
      "components" : [ {
        "name" : "#2",
        "type" : "TOKEN"
      } ]
    }, {
      "type" : "CODE_BLOCK",
      "components" : [ {
        "type" : "DECLARATION",
        "components" : [ {
          "name" : "EITHER",
          "type" : "TYPE",
          "components" : [ {
            "name" : "MUTABLE",
            "type" : "TYPE",
            "components" : [ {
              "name" : "EITHER",
              "type" : "TYPE",
              "components" : [ {
                "name" : "INT64",
                "type" : "TYPE"
              }, {
                "name" : "NOTHING",
                "type" : "TYPE"
              } ]
            } ]
          }, {
            "name" : "NOTHING",
            "type" : "TYPE"
          } ]
        }, {
          "name" : "!x",
          "type" : "TOKEN"
        }, {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!list",
            "type" : "TOKEN"
          }, {
            "name" : "ELEM_ACCESS",
            "type" : "TOKEN"
          }, {
            "name" : "!i",
            "type" : "TOKEN"
          } ]
        } ]
      }, {
        "type" : "WHEN",
        "components" : [ {
          "type" : "EXPRESSION",
          "components" : [ {
            "name" : "!x",
            "type" : "TOKEN"
          } ]
        }, {
          "name" : "MUTABLE",
          "type" : "TYPE",
          "components" : [ {
            "name" : "NOTHING",
            "type" : "TYPE"
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!text",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!text",
                "type" : "TOKEN"
              }, {
                "name" : "STRING|PLUS|STRING",
                "type" : "TOKEN"
              }, {
                "name" : "@mut[nothing],",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        }, {
          "name" : "NOTHING",
          "type" : "TYPE"
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!text",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!text",
                "type" : "TOKEN"
              }, {
                "name" : "STRING|PLUS|STRING",
                "type" : "TOKEN"
              }, {
                "name" : "@nothing,",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        }, {
          "name" : "ELSE",
          "type" : "TYPE",
          "components" : [ {
            "name" : "MUTABLE",
            "type" : "TYPE",
            "components" : [ {
              "name" : "INT64",
              "type" : "TYPE"
            } ]
          } ]
        }, {
          "type" : "CODE_BLOCK",
          "components" : [ {
            "type" : "EXPRESSION",
            "components" : [ {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!text",
                "type" : "TOKEN"
              } ]
            }, {
              "name" : "ASSIGN",
              "type" : "TOKEN"
            }, {
              "type" : "EXPRESSION",
              "components" : [ {
                "name" : "!text",
                "type" : "TOKEN"
              }, {
                "name" : "STRING|PLUS|STRING",
                "type" : "TOKEN"
              }, {
                "name" : "@default,",
                "type" : "TOKEN"
              } ]
            } ]
          } ]
        } ]
      } ]
    } ]
  } ]
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
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
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
            "name" : "INT64",
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
          "type" : "DECLARATION",
          "components" : [ {
            "name" : "INT64",
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
            "name" : "INT64",
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
                "name" : "INT64|PLUS|INT64",
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
      "name" : "INT64",
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
      "name" : "INT64",
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
        assertVar(vars, "!a", 30L)
        assertVar(vars, "!b", 21L)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            Main.DEBUG = true
        }
    }
}