package wiles.shared.abstracts

import wiles.shared.data.TokenLocation
import wiles.shared.errors.NoTokenLocationException

interface LocationAccessibleInterface {
    val location : TokenLocation?

    fun getComponents(): List<LocationAccessibleInterface>

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