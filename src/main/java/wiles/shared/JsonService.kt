package wiles.shared

import java.io.File

object JsonService {
    fun readValueAsJSONStatement(file: File): JSONStatement {
        return readValueAsJSONStatement(file.readText())
    }

    fun writeValue(file: File, statement: StatementInterface) {
        file.writeText(writeValueAsString(statement))
    }

    fun readValueAsJSONStatement(text: String): JSONStatement {
        TODO("Not yet implemented")
    }

    fun writeValueAsString(statement: StatementInterface): String
    {
        TODO("Not yet implemented")
    }
}