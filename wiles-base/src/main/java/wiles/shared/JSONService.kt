package wiles.shared

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.WriterConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

object JSONService {
    private const val PARSED = "parsed"
    private const val NAME = "name"
    private const val TYPE = "type"
    private const val LOCATION = "location"
    private const val COMPONENTS = "components"
    private const val LINE = "line"
    private const val LINE_END = "lineEnd"
    private const val LINE_INDEX = "lineIndex"
    private const val LINE_END_INDEX = "lineEndIndex"

    fun readValueAsJSONStatement(file: File): JSONStatement {
        try {
            val text = file.readText()
            return readValueAsJSONStatement(text)
        } catch (ex: IOException) {
            throw InternalErrorException(ex.toString())
        }
    }

    fun writeValue(file: File, statement: StatementInterface) {
        try {
            FileWriter(file).use { writer -> writer.append(writeValueAsString(statement)) }
        } catch (ex: IOException) {
            throw InternalErrorException(ex.toString())
        }
    }

    fun readValueAsJSONStatement(text: String): JSONStatement {
        //name
        val statement = JSONStatement()
        val obj = Json.parse(text).asObject()
        val name = obj[NAME]
        if (name != null) statement.name = name.asString()

        //parsed
        val parsed = obj[PARSED]
        if (parsed != null) statement.parsed = parsed.asBoolean() else statement.parsed = null

        //type
        val type = Objects.requireNonNull(obj[TYPE])
        statement.syntaxType = SyntaxType.valueOf(type.asString())

        //location
        val location = obj[LOCATION]
        if (location != null) {
            val locationObject = location.asObject()
            val line = Objects.requireNonNull(locationObject[LINE]).asInt()
            val lineIndex = Objects.requireNonNull(locationObject[LINE_INDEX]).asInt()
            val lineEnd = locationObject[LINE_INDEX]?.asInt()
            val lineEndIndex = locationObject[LINE_END_INDEX]?.asInt()
            val tokenLocation = TokenLocation(line, lineIndex, lineEnd ?: -1, lineEndIndex ?: -1)
            statement.location = tokenLocation
        }

        //components
        val components = obj[COMPONENTS]
        if (components != null) {
            val array = components.asArray()
            for (component in array.values()) {
                val newComponent = readValueAsJSONStatement(component.toString())
                statement.components.add(newComponent)
            }
        }

        return statement
    }

    private fun getJsonObjectFromStatement(statement: StatementInterface): JsonObject {

        //name
        val value = Json.`object`()
        val name = statement.name
        if (name != "") value.add(NAME, name)

        //parsed
        val parsed = statement.parsed
        if (parsed != null) value.add(PARSED, parsed)

        //type
        val type = Objects.requireNonNull(statement.syntaxType).toString()
        value.add(TYPE, type)

        //location
        val location = statement.location
        if (location != null) {
            val obj = Json.`object`()
            obj.add(LINE, location.line)
            obj.add(LINE_END, location.lineEnd)
            obj.add(LINE_INDEX, location.lineIndex)
            obj.add(LINE_END_INDEX, location.lineEndIndex)
            value.add(LOCATION, obj)
        }

        //components
        val components: List<StatementInterface> = statement.getComponents()
        if (components.isNotEmpty()) {
            val array = JsonArray()
            for (component in components) {
                array.add(getJsonObjectFromStatement(component))
            }
            value.add(COMPONENTS, array)
        }

        return value
    }

    fun writeValueAsString(statement: StatementInterface): String {
        return getJsonObjectFromStatement(statement).toString(WriterConfig.PRETTY_PRINT)
    }
}