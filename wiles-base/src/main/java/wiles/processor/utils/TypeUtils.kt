package wiles.processor.utils

import wiles.processor.enums.WilesTypes
import wiles.processor.types.AbstractType
import wiles.processor.types.DecimalType
import wiles.processor.types.IntegerType
import wiles.processor.types.StringType

object TypeUtils {
    private fun checkSingletonStatus(former : AbstractType, latter : AbstractType): Boolean {
        if(former.isSingleton() && !latter.isSingleton())
            return false
        if(former.isSingleton() && latter.isSingleton())
            return former.getValue() == latter.getValue()
        return true
    }

    fun isSuperType(superType : AbstractType, subType : AbstractType): Boolean {
        if(superType.typeName == WilesTypes.INVALID || superType.typeName == WilesTypes.INVALID)
            return false
        if(subType.typeName == WilesTypes.NOTHING)
        {
            return superType.typeName == WilesTypes.NOTHING
        }
        if(superType.typeName == subType.typeName)
        {
            if(!checkSingletonStatus(superType, subType))
                return false
            return true
        }
        return false
    }

    val INTEGER_TYPE = IntegerType()
    val STRING_TYPE = StringType()
    val DECIMAL_TYPE = DecimalType()
}