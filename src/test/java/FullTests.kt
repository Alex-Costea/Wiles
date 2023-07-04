import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import wiles.Main.main
import wiles.shared.constants.CommandLineArguments
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals


class FullTests {

    private val systemIn = System.`in`
    private val systemOut = System.out
    private fun getOutput(code : String) : String
    {
        val baos = ByteArrayOutputStream()
        val outputStream = PrintStream(baos)
        System.setOut(outputStream)
        main(arrayOf(code, CommandLineArguments.NO_INPUT_FILE_COMMAND))
        System.setIn(systemIn)
        System.setOut(systemOut)
        return baos.toString("UTF-8")
    }

    @Test
    fun fullTest()
    {
        val code = """
            
typedef number := either[int,rational]

let min := fun(list : list[number as T]) -> T?
begin
    if list.size = 0 do yield nothing
    let var min_value := list.get(0)
    for x in list from 1 do
        if x < min_value do
            min_value := x
    yield min_value
end

let read_list := begin
    let list := mut [] : int
    let list_size := read_int()
    for i from 0 to list_size
    begin
        list.add(at := i, read_int())
    end
    yield list
end

let result := min(list := read_list())
when result is nothing
begin
    writeline("Error: no min found!")
    panic()
end
writeline("Min found: " + result)
"""

        assertEquals(getOutput(code),"Min found: -1\n")
        assertEquals(getOutput(code),"Error: no min found!\n")

        val code2 = """
            let list := [1,2,3]
            let text := "[1,2,3]"
            writeline(list.size + text.size)
"""
        assertEquals(getOutput(code2),"10\n")

        val code3 = """
            typedef integer := int
            let a : integer := 10
            writeline(integer)"""
        assertEquals(getOutput(code3),"TYPE INT64\n")

        val code4 = """
            let var a := 10
            let func := do writeline(import a)
            a := 100
            func()
        """
        assertEquals(getOutput(code4),"100\n")

        val code5 = """
            let a := mut 2
            let list := mut [mut 1, a, mut 3]
            list.remove(at := 0)
            list.update(at := 0, mut 10)
            writeline(a)
            writeline(list)
        """
        assertEquals(getOutput(code5),"2\n[10, 3]\n")

        val code6 = """
            let var a := 10
            let func := do import a := 100
            writeline(a)
            func()
            writeline(a)
        """
        assertEquals(getOutput(code6),"10\n100\n")

        val code7 = """
            let a := 10
            let b := fun(b := import a) do writeline(b)
            b()
        """
        assertEquals(getOutput(code7),"10\n")

        val code8 = """
        let a := mut [1] : anything
        a.add(at := a.size, true)
        when a begin
            is mut[list[int]] begin
                let b := a.get(1)
                writeline(b + 10)
            end
            default do writeline("not mut[list[int]]")
        end
        """
        assertEquals(getOutput(code8),"not mut[list[int]]\n")

        val code9 = """
        let var a : anything := 1
        a := true
        writeline(a.type)
        """
        assertEquals(getOutput(code9),"TYPE BOOLEAN\n")

        val code10 = """
        let list := [1,2,3]
        writeline(list.get(0))
        """
        assertEquals(getOutput(code10),"1\n")

        val code11 = """
            let func := fun(arg list : list[int])
        begin
            let object : anything := list
            when object is mut[list[text]]
            begin
                object.add(at := 0, "hi!")
                yield nothing
            end
            writeline("oops!")
            panic()
        end
        
        let list := mut [] : int
        func(list)
        let a := list.get(0)
        writeline(a>0)
        """
        assertEquals(getOutput(code11),"oops!\n")

        val code12 = """
        let dict := mut {"alex" -> 7, "diana" -> 10}
        dict.add(at := "jim", 15)
        dict.update(at := "alex", 3)
        writeline(dict.get(at := "diana"))
        dict.remove(at := "diana")
        writeline(dict)
        """
        assertEquals(getOutput(code12),"10\n{alex=3, jim=15}\n")

        val code13 = """
        let a := mut {1 -> "hi!", 2 -> "bye!"} : int? -> text?
        writeline(a.keys)
        writeline(a.type)
        a.add(at := nothing, nothing)
        writeline(a.keys)
        writeline(a.type)
        a.remove(at := nothing)
        writeline(a.keys)
        writeline(a.type)
        """
        assertEquals(getOutput(code13),"[1, 2]\n" +
                "TYPE MUTABLE; (TYPE DICT; (TYPE INT64; TYPE STRING))\n" +
                "[1, 2, nothing]\n" +
                "TYPE MUTABLE; (TYPE DICT; (TYPE EITHER; (TYPE INT64; TYPE !nothing); TYPE EITHER; (TYPE STRING; TYPE !nothing)))\n" +
                "[1, 2]\n" +
                "TYPE MUTABLE; (TYPE DICT; (TYPE INT64; TYPE STRING))\n")

        val code14 = """
        let a := mut {1 -> "hi!", 2 -> "bye!"} : int? -> text?
        let b := mut {1 -> "hi!", 2 -> "bye!"}
        writeline(a=b)
        """
        assertEquals(getOutput(code14),"true\n")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpIO() {
            System.setIn(("4\n10\n8\n20\n-1\n" + "0\n").byteInputStream())
        }
    }
}