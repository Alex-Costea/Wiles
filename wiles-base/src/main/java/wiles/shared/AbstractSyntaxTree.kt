package wiles.shared

data class AbstractSyntaxTree(
    val details : List<String>,
    val syntaxType: SyntaxType,
    private val location: TokenLocation?,
    val components : List<AbstractSyntaxTree>
){
    fun getFirstLocation() : TokenLocation
    {
        val location = location
        if(location!= null)
            return location
        else for(part in components)
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
