import org.junit.jupiter.api.Test
import wiles.Main
import wiles.shared.constants.CommandLineArguments.NO_INPUT_FILE_COMMAND
import kotlin.test.assertEquals

class FullTest {
    @Test
    fun fullTests()
    {
        Main.main(arrayOf(NO_INPUT_FILE_COMMAND,"""
let min := fun(list : list[int]) -> int?
begin
    let var min_value := list @ 0
    when min_value is int do
        for x in list from 1 do
            if x < min_value do
                min_value := x
    yield min_value
end

let result := min(list := [10, 3, 55, 8])
when result is begin
    int do writeline("Min found: " + result)
    default do panic("Error: no min found!")
end"""))
        assertEquals(Main.finalCode,"""CODE_BLOCK(DECLARATION(TYPE METHOD; (METHOD(TYPE EITHER; (TYPE INT64; TYPE NOTHING); DECLARATION(TYPE LIST; (TYPE INT64); !list))); !min; EXPRESSION(TYPE METHOD; (METHOD(TYPE EITHER; (TYPE INT64; TYPE NOTHING); DECLARATION(TYPE LIST; (TYPE INT64); !list))); METHOD(TYPE EITHER; (TYPE INT64; TYPE NOTHING); DECLARATION(TYPE LIST; (TYPE INT64); !list); CODE_BLOCK(DECLARATION VARIABLE; (TYPE EITHER; (TYPE INT64; TYPE NOTHING); !min_value; EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !list; LIST|ELEM_ACCESS|INT64; #0)); WHEN(EXPRESSION(!min_value); TYPE INT64; CODE_BLOCK(FOR(TYPE INT64; !x; IN; EXPRESSION(TYPE LIST; (TYPE INT64); !list); FROM; EXPRESSION(TYPE INT64; #1); CODE_BLOCK(IF(EXPRESSION(TYPE BOOLEAN; !x; INT64|SMALLER|INT64; !min_value); CODE_BLOCK(EXPRESSION(TYPE NOTHING; EXPRESSION(TYPE INT64; !min_value); INT64|ASSIGN|INT64; EXPRESSION(TYPE INT64; !x)))))))); RETURN(EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !min_value)))))); DECLARATION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !result; EXPRESSION(TYPE EITHER; (TYPE INT64; TYPE NOTHING); !min; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!list; ASSIGN; EXPRESSION(TYPE LIST; (TYPE INT64); LIST(TYPE INT64; EXPRESSION(TYPE INT64; #10); EXPRESSION(TYPE INT64; #3); EXPRESSION(TYPE INT64; #55); EXPRESSION(TYPE INT64; #8))))))); WHEN(EXPRESSION(!result); TYPE INT64; CODE_BLOCK(EXPRESSION(TYPE NOTHING; !writeline; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE STRING; @Min found:; STRING|PLUS|INT64; !result))))); ELSE; CODE_BLOCK(EXPRESSION(TYPE NOTHING; !panic; METHOD|APPLY|METHOD_CALL; METHOD_CALL(EXPRESSION(!text; ASSIGN; EXPRESSION(TYPE STRING; @Error: no min found!)))))))""")
    }
}