package wiles.processor.data

import wiles.processor.types.AbstractType
import wiles.processor.values.WilesLazyObject

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val comptimeType : AbstractType = type
    ) {
    fun getObj() : Any?{
        if(obj is WilesLazyObject)
            return obj.getObject()
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    fun getComptimeType() : AbstractType{
        return comptimeType
    }

    fun isLazy(): Boolean {
        return obj is WilesLazyObject
    }

    private fun getObjString() : String
    {
        if((obj as? WilesLazyObject)?.hasBeenComputed() == false) return "LazyObject"
        return getObj().toString()
    }

    override fun toString(): String {
        return "Value(obj=${getObjString()}, type=$type, comptimeType=$comptimeType)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (getObj() != other.getObj()) return false
        if (type != other.type) return false
        if (comptimeType != other.comptimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = getObj()?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + comptimeType.hashCode()
        return result
    }

    fun isKnown() : Boolean {
        return obj != null
    }

}