package wiles.processor.utils

import wiles.processor.enums.WilesTypes
import wiles.processor.types.AbstractType

object TypeUtils {
    private fun checkSingletonStatus(former : AbstractType, latter : AbstractType): Boolean {
        if(former.isSingleton() && !latter.isSingleton())
            return false
        if(former.isSingleton() && latter.isSingleton())
            return former.getValue() == latter.getValue()
        return true
    }

    fun isFormerSuperTypeOfLatter(former : AbstractType, latter : AbstractType): Boolean {
        if(former.typeName == WilesTypes.INVALID || former.typeName == WilesTypes.INVALID)
            return false
        if(latter.typeName == WilesTypes.NOTHING)
        {
            return former.typeName == WilesTypes.NOTHING
        }
        if(former.typeName == latter.typeName)
        {
            if(!checkSingletonStatus(former, latter))
                return false
            return true
        }
        return false
    }
}