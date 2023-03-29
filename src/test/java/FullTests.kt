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

    private fun setUpIO(input : String)
    {
        System.setIn(input.byteInputStream())
    }

    @Test
    fun fullTest()
    {
        val code = """let min := fun(list : list[int]) -> int?
begin
    let var min_value := list @ 0
    when min_value is int
    begin
        for x in list from 1 do
            if x < min_value do
                min_value := x
    end
    yield min_value
end

let read_list := begin
    let var list := [] : int
    let list_size := read_int()
    for i from 0 to list_size do
        list := list + [read_int()]
    yield list
end

let result := min(list := read_list())
when result is begin
    int do writeline("Min found: " + result)
    default do writeline("Error: no min found!")
end"""
        //TODO: refactor
        setUpIO("4\n10\n8\n20\n-1\n" + "0\n")

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
            let list := mut [1, a, 3]
            list.remove(index := 0)
            list.set_at(index := 0, 10)
            writeline(a)
            writeline(list)
        """
        assertEquals(getOutput(code5),"10\n[10, 3]\n")

        val code5v2 = """
            let a := mut 2
            let list := mut [1, a, 3]
            list.remove(index := 0)
            list.set_at(index := 0, 10, mutate := false)
            writeline(a)
            writeline(list)
        """
        assertEquals(getOutput(code5v2),"2\n[10, 3]\n")

        val code6 = """
            let var a := 10
            let func := do import a := 100
            writeline(a)
            func()
            writeline(a)
        """
        assertEquals(getOutput(code6),"10\n100\n")
    }
}