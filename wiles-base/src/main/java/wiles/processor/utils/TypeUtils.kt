package wiles.processor.utils

import wiles.processor.enums.WilesTypes
import wiles.processor.types.*

object TypeUtils {
    private fun checkSingletonStatus(former : AbstractType, latter : AbstractType): Boolean {
        if(former.isSingleton() && !latter.isSingleton())
            return false
        if(former.isSingleton() && latter.isSingleton())
            return former.getValue() == latter.getValue()
        return true
    }

    fun isSuperType(superType : AbstractType, subType : AbstractType): Boolean {
        return when {
            superType.typeName == WilesTypes.INVALID || superType.typeName == WilesTypes.INVALID -> false
            subType.typeName == WilesTypes.NOTHING -> superType.typeName == WilesTypes.NOTHING
            superType.typeName == WilesTypes.ANYTHING -> true
            superType.typeName == subType.typeName -> checkSingletonStatus(superType, subType)
            else -> false
        }
    }

    val INTEGER_TYPE = IntegerType()
    val STRING_TYPE = TextType()
    val DECIMAL_TYPE = DecimalType()
    val TYPE_TYPE = TypeType()
}