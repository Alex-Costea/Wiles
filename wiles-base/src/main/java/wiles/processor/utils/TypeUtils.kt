package wiles.processor.utils

import wiles.processor.enums.WilesTypes
import wiles.processor.types.AbstractType

object TypeUtils {
    private fun checkExactStatus(former : AbstractType, latter : AbstractType): Boolean {
        if(former.isExact() && !latter.isExact())
            return false
        if(former.isExact() && latter.isExact())
            return former.getValue() == latter.getValue()
        return true
    }

    fun isSuperType(superType : AbstractType, subType : AbstractType): Boolean {
        return when {
            superType.typeName == WilesTypes.INVALID || superType.typeName == WilesTypes.INVALID -> false
            subType.typeName == WilesTypes.NOTHING -> superType.typeName == WilesTypes.NOTHING
            superType.typeName == WilesTypes.ANYTHING -> true
            superType.typeName == subType.typeName -> checkExactStatus(superType, subType)
            else -> false
        }
    }
}