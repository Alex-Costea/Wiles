package wiles.processor.utils

import wiles.processor.data.*
import wiles.processor.enums.VariableStatus
import wiles.processor.functions.WilesFunction
import wiles.processor.processors.ProcessorTypeExpression
import wiles.processor.types.*
import wiles.processor.types.AbstractType.Companion.INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.MINUS_INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.values.*
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.errors.InternalErrorException

object InterpreterUtils {
    fun isSuperType(superType: WilesType, subType: WilesType) : Boolean
    {
        for(type2 in subType.getSubtypes())
        {
            var hasMatch = false
            for(type1 in superType.getSubtypes())
            {
                if(isComponentSuperType(type1, type2))
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

    fun isComponentSuperType(superType : AbstractType, subType : AbstractType): Boolean {
        return when {
            superType is InvalidType || subType is InvalidType -> false
            subType is NothingType -> superType is NothingType
            superType is AnythingType -> true
            superType.javaClass == subType.javaClass -> checkExactStatus(superType, subType)
            else -> false
        }
    }

    private fun checkExactStatus(former : AbstractType, latter : AbstractType): Boolean {
        if(former.isExact() && !latter.isExact())
            return false
        if(former.isExact() && latter.isExact())
            return former.getValue() == latter.getValue()
        return true
    }

    private fun getBooleanType(boolean: Boolean) =
        if(boolean) AbstractType.TRUE_TYPE else AbstractType.FALSE_TYPE

    fun getNewTypeObject(value : Value) : WilesType{
        val defaultType = value.getType()
        return when(val obj = value.getObj()) {
            is WilesInteger -> WilesType(IntType(obj))
            is WilesDecimal -> WilesType(DecimalType(obj))
            is WilesNothing -> NOTHING_TYPE
            is String -> WilesType(TextType(obj))
            is Boolean -> getBooleanType(obj)
            is WilesType -> obj
            is WilesFunction -> defaultType
            is WilesInfinity -> INFINITY_TYPE
            is WilesMinusInfinity -> MINUS_INFINITY_TYPE
            null -> defaultType
            else -> throw InternalErrorException()
        }
    }

    fun filterOutImpure(values : ValuesMap, pure : Boolean): ValuesMap {
        if(!pure) return ValuesMap(values)
        val newValues = ValuesMap()
        //TODO: remove types that have the following as subtypes
        // List, Dict, Anything
        // Also check the types recursively to not have a disallowed element
        // e.g. <<a : Mutable(List(Int)))>>
        for((key, valueData ) in values)
        {
            val value = valueData.value
            if(valueData.isVariable())
                continue
            val obj = value.getObj()
            if(obj is WilesFunction && !obj.pure)
                continue
            newValues[key] = valueData
        }
        return newValues
    }

    fun processType(typeDef : AbstractSyntaxTree, context : InterpreterContext): WilesType {
        val typeProcessor = ProcessorTypeExpression(typeDef, context)
        val typeDefValue = typeProcessor.process()
        assert(typeDefValue.isKnown())
        assert(isSuperType(TYPE_TYPE,typeDefValue.getType()))
        return typeDefValue.getObj() as WilesType
    }

    fun getCompilerValues(compilerValues: ValuesMap) : ValuesMap
    {
        val newValues = ValuesMap()
        for((name, valueData) in compilerValues)
        {
            val value = valueData.value
            if(!value.isKnown())
                newValues[name] = ValueData(Value(null, valueData.getComptimeType()),
                    valueData.variableStatus)
            else if(valueData.isVariable())
                newValues[name] = ValueData(Value( null, valueData.getComptimeType()), VariableStatus.Var)
            else newValues[name] = valueData
        }
        return newValues
    }

    fun getYieldedType(possibilities: List<YieldPossibility>?): WilesType {
        if(possibilities.isNullOrEmpty())
            return NOTHING_TYPE
        val types = mutableListOf<AbstractType>()
        for(possibility in possibilities)
        {
            types.add(possibility.type)
        }
        val typesArray : Array<AbstractType> = types.toTypedArray()
        return WilesType(*typesArray)
    }

    fun equalsValue(obj: Any?, equals : String): Boolean {
        if(obj is WilesInteger)
        {
            return obj == WilesInteger(equals)
        }
        if(obj is WilesDecimal)
        {
            return obj == WilesDecimal(equals)
        }
        return false
    }

    fun objToStringInternally(obj : Any?) : String
    {
        return if(obj is String) "\"$obj\"" else obj.toString()
    }

}