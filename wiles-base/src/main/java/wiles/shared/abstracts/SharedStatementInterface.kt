package wiles.shared.abstracts

import wiles.shared.data.TokenLocation
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.NoTokenLocationException

interface SharedStatementInterface {

    val syntaxType: SyntaxType?

    fun getStatementName() : String

    val location : TokenLocation?

    fun getComponents(): List<SharedStatementInterface>

    fun getFirstLocation() : TokenLocation
    {
        val location = location
        if(location!= null)
            return location
        else for(component in getComponents())
        {
            try
            {
                return component.getFirstLocation()
            }
            catch (_: NoTokenLocationException) {}
        }
        throw NoTokenLocationException()
    }

}