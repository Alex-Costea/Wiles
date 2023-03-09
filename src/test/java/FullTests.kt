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
    fun minTest()
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
    let list := mut [] : int
    let list_size := read_int()
    for i from 0 to list_size do
        list += read_int()
    yield list
end

let result := min(list := read_list())
when result is begin
    int do writeline("Min found: " + result)
    default do writeline("Error: no min found!")
end"""
        //TODO: refactor
        setUpIO("4\n10\n8\n20\n-1\n"+ "0\n")
        assertEquals(getOutput(code),"Min found: -1\n")
        assertEquals(getOutput(code),"Error: no min found!\n")
    }
}