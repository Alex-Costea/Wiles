package wiles.shared

interface StatementInterface {
    val location : TokenLocation?

    fun getComponents(): MutableList<out StatementInterface>

    var name: String

    val syntaxType: SyntaxType?
    fun getFirstLocation() : TokenLocation
    {
        val location = location
        if(location!= null)
            return location
        else for(part in getComponents())
        {
            try
            {
                return part.getFirstLocation()
            }
            catch (_: InternalErrorException) {}
        }
        throw InternalErrorException("No token locations found!")
    }
}