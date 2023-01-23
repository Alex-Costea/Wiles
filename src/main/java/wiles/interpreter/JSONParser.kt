package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object JSONParser
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        val mapper = ObjectMapper()
        val obj: JSONStatement = mapper.readValue(File("syntaxTree.json"), JSONStatement::class.java)
        println(obj)
    }
}