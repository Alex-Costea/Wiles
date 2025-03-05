package wiles.processor.utils

import wiles.processor.data.Value
import wiles.processor.types.*
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

    private fun checkEither(superType: AbstractType, subType: AbstractType) : Boolean
    {
        val superEither = if(superType is EitherType) superType else EitherType(superType)
        val subEither = if(subType is EitherType) subType else EitherType(subType)
        for(type2 in subEither.getSubtypes())
        {
            var hasMatch = false
            for(type1 in superEither.getSubtypes())
            {
                if(isSuperType(type1, type2))
                {
                    hasMatch = true
                    break
                }
            }
            if(!hasMatch)
                return false
        }
        return true
    }

    fun isSuperType(superType : AbstractType, subType : AbstractType): Boolean {
        return when {
            superType is InvalidType || subType is InvalidType -> false
            superType is EitherType || subType is EitherType-> checkEither(superType, subType)
            subType is NothingType -> superType is NothingType
            superType is AnythingType -> true
            superType.javaClass == subType.javaClass -> checkExactStatus(superType, subType)
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