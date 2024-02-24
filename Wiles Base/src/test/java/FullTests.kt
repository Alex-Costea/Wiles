
import org.junit.jupiter.api.Test
import wiles.WilesCompiler.main
import wiles.shared.constants.CommandLineArguments
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals


class FullTests {

    private val systemIn = System.`in`
    private val systemOut = System.out
    private fun getOutput(code : String, input : String? = null) : String
    {
        val baos = ByteArrayOutputStream()
        val outputStream = PrintStream(baos)
        System.setOut(outputStream)
        val args = mutableListOf(CommandLineArguments.CODE_COMMAND + code)
        if(input != null) {
            args.add(0,CommandLineArguments.INPUT_COMMAND+input)
        }
        main(args.toTypedArray())
        System.setIn(systemIn)
        System.setOut(systemOut)
        return baos.toString("UTF-8")
    }

    @Test
    fun functionVariableScopeTests()
    {
        val code1="""
            let a := 123
            let f := fun(d := 15)
            begin
                let c := 1
                write_line(d)
                yield a + 5
            end
            let c := 2
            let d := 20
            write_line(f() + c + d)
        """
        assertEquals(getOutput(code1),"15\n150\n")

        val code2="""
            let var a := 100
            let func := do yield a + 5
            a := 15
            write_line(func())
        """
        assertEquals(getOutput(code2),"20\n")
    }

    @Test
    fun fullTest()
    {
        val code = """
            
typedef number := int or rational

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
    write_line("Error: no min found!")
    panic()
end
write_line("Min found: " + result)
"""

        assertEquals(getOutput(code,"4\n" +
                "10\n" +
                "8\n" +
                "20\n" +
                "-1\n"),"Min found: -1\n")
        assertEquals(getOutput(code,"0\n"),"Error: no min found!\n")

        val code2 = """
            let list := [1,2,3]
            let text := "[1,2,3]"
            write_line(list.size + text.size)
"""
        assertEquals(getOutput(code2),"10\n")

        val code3 = """
            typedef integer := int
            let a : integer := 10
            write_line(integer)"""
        assertEquals(getOutput(code3),"TYPE INT\n")

        val code4 = """
            let var a := 10
            let func := do write_line(import a)
            a := 100
            func()
        """
        assertEquals(getOutput(code4),"100\n")

        val code5 = """
            let a := mut 2
            let list := mut [mut 1, a, mut 3]
            list.remove(at := 0)
            list.update(at := 0, mut 10)
            write_line(a)
            write_line(list)
        """
        assertEquals(getOutput(code5),"2\n[10, 3]\n")

        val code6 = """
            let var a := 10
            let func := do import a := 100
            write_line(a)
            func()
            write_line(a)
        """
        assertEquals(getOutput(code6),"10\n100\n")

        val code7 = """
            let a := 10
            let b := fun(b := import a) do write_line(b)
            b()
        """
        assertEquals(getOutput(code7),"10\n")

        val code8 = """
        let a := mut [1] : anything
        a.add(at := a.size, true)
        when a begin
            is mut[list[int]] begin
                let b := a.get(1)
                write_line(b + 10)
            end
            default do write_line("not mut[list[int]]")
        end
        """
        assertEquals(getOutput(code8),"not mut[list[int]]\n")

        val code9 = """
        let var a : anything := 1
        a := true
        write_line(a.type)
        """
        assertEquals(getOutput(code9),"TYPE BOOLEAN\n")

        val code10 = """
        let list := [1,2,3]
        write_line(list.get(0))
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
            write_line("oops!")
            panic()
        end
        
        let list := mut [] : int
        func(list)
        let a := list.get(0)
        write_line(a>0)
        """
        assertEquals(getOutput(code11),"oops!\n")

        val code12 = """
        let dict := mut {"alex" -> 7, "diana" -> 10}
        dict.add(at := "jim", 15)
        dict.update(at := "alex", 3)
        write_line(dict.get(at := "diana"))
        dict.remove(at := "diana")
        write_line(dict)
        """
        assertEquals(getOutput(code12),"10\n{alex -> 3, jim -> 15}\n")

        val code13 = """
        let a := mut {1 -> "hi!", 2 -> "bye!"} : int? -> text?
        write_line(a.keys)
        write_line(a.type)
        a.add(at := nothing, nothing)
        write_line(a.keys)
        write_line(a.type)
        a.remove(at := nothing)
        write_line(a.keys)
        write_line(a.type)
        """
        assertEquals(getOutput(code13),"[1, 2]\n" +
                "TYPE MUTABLE; (TYPE DICT; (TYPE INT; TYPE STRING))\n" +
                "[1, 2, nothing]\n" +
                "TYPE MUTABLE; (TYPE DICT; (TYPE EITHER; (TYPE INT; TYPE !nothing); TYPE EITHER; (TYPE STRING; TYPE !nothing)))\n" +
                "[1, 2]\n" +
                "TYPE MUTABLE; (TYPE DICT; (TYPE INT; TYPE STRING))\n")

        val code14 = """
        let a := mut {1 -> "hi!", 2 -> "bye!"} : int? -> text?
        let b := mut {1 -> "hi!", 2 -> "bye!"}
        write_line(a=b)
        """
        assertEquals(getOutput(code14),"true\n")

        val code15 = """
        let b := {} : int -> int
        write_line(b.type)
        """
        assertEquals(getOutput(code15),"TYPE DICT; (TYPE INT; TYPE INT)\n")

        val code16= """
        let b := {"hi" -> 1, "bye!" -> 2}
        write_line(b)
        """
        assertEquals(getOutput(code16),"{hi -> 1, bye! -> 2}\n")

        val code17 = """
            for i from 9 to 0 do write(i)
        """
        assertEquals(getOutput(code17),"987654321")

        val code18 = """
            let a := fun(x : int or text? as T) do nothing
            write_line(a)
        """
        assertEquals(getOutput(code18),"METHOD(TYPE !nothing; DECLARATION(TYPE GENERIC; (!T|1; TYPE EITHER; (TYPE INT; TYPE EITHER; (TYPE STRING; TYPE !nothing)); DECLARE); !x))\n")

        val code19="""
            let f := fun(x : int) do write_line(x)

            when f is fun[x : int] begin
                f(x := 10)
            end
        """
        assertEquals(getOutput(code19),"10\n")

        val code20 ="""
            typedef int2 := int
            let x := 2
            when x is int2 do write_line("hi")
        """
        assertEquals(getOutput(code20),"hi\n")

        val code21="""
            let me := data{name := "alex", age := 25}
            write_line(me.name)
            write_line(me.age)
            write_line(me.type)
        """
        assertEquals(getOutput(code21),"alex\n" +
                "25\n" +
                "TYPE DATA; (!name; TYPE STRING; !age; TYPE INT)\n")

        val code22="""
            typedef has_name := data[name : text]
            let me : has_name := data{name := "alex", age := 25}
            when me is has_name do write_line(me)
        """
        assertEquals(getOutput(code22),"{name -> alex, age -> 25}\n")

        val code23="""
            let x := data{y := 2 + 3}
            write_line(x.y)
        """
        assertEquals(getOutput(code23),"5\n")

        val code24="""
            let ❤️ := data{😂 := 5, 🏳️‍🌈 := 25, 👩‍👩‍👧‍👧 := 125}
            write_line(❤️.😂)
            write_line(❤️.🏳️‍🌈)
            write_line(❤️.👩‍👩‍👧‍👧)
        """
        assertEquals(getOutput(code24),"5\n25\n125\n")
    }
}