package wiles.processor.utils

import wiles.processor.data.Value
import wiles.processor.enums.WilesTypes
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.BOOLEAN_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INTEGER_TYPE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesNothing
import wiles.shared.errors.InternalErrorException

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
            superType.typeName == WilesTypes.Invalid || superType.typeName == WilesTypes.Invalid -> false
            subType.typeName == WilesTypes.Nothing -> superType.typeName == WilesTypes.Nothing
            superType.typeName == WilesTypes.Anything -> true
            superType.typeName == subType.typeName -> checkExactStatus(superType, subType)
            else -> false
        }
    }

    fun getNewTypeObject(value : Value) : AbstractType{
        return when(val obj = value.getObj()) {
            is WilesInteger -> INTEGER_TYPE.exactly(obj)
            is WilesDecimal -> DECIMAL_TYPE.exactly(obj)
            is WilesNothing -> NOTHING_TYPE
            is String -> TEXT_TYPE.exactly(obj)
            is Boolean -> BOOLEAN_TYPE.exactly(obj)
            is AbstractType -> obj
            else -> throw InternalErrorException()
        }
    }

}