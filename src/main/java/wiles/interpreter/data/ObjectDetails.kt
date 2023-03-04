package wiles.interpreter.data

import wiles.shared.JSONStatement

data class ObjectDetails(val value : Any?, val type : JSONStatement)
{
    fun clone() : ObjectDetails
    {
        //TODO: deep copy for value?
        return ObjectDetails(this.value, this.type.copyRemovingLocation())
    }
}