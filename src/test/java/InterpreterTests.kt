
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

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            Main.DEBUG = true
        }
    }
}