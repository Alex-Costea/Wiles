package wiles.processor.utils

import wiles.processor.data.*
import wiles.processor.enums.VariableStatus
import wiles.processor.functions.WilesFunction
import wiles.processor.processors.ProcessorTypeExpression
import wiles.processor.types.*
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesNothing
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.errors.InternalErrorException

object InterpreterUtils {
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

    private fun getBooleanType(boolean: Boolean) =
        if(boolean) AbstractType.TRUE_TYPE else AbstractType.FALSE_TYPE


    fun getNewTypeObject(value : Value) : AbstractType{
        val defaultType = value.getType()
        return when(val obj = value.getObj()) {
            is WilesInteger -> INT_TYPE.exactly(obj)
            is WilesDecimal -> DECIMAL_TYPE.exactly(obj)
            is WilesNothing -> NOTHING_TYPE
            is String -> TEXT_TYPE.exactly(obj)
            is Boolean -> getBooleanType(obj)
            is AbstractType -> obj
            is WilesFunction -> defaultType
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

    fun processType(typeDef : AbstractSyntaxTree, context : InterpreterContext): AbstractType {
        val typeProcessor = ProcessorTypeExpression(typeDef, context)
        val typeDefValue = typeProcessor.process()
        assert(typeDefValue.isKnown())
        assert(isSuperType(TYPE_TYPE,typeDefValue.getType()))
        return typeDefValue.getObj() as AbstractType
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

    fun getYieldedType(possibilities: List<YieldPossibility>?): AbstractType {
        if(possibilities == null)
            return NOTHING_TYPE
        val types = mutableListOf<AbstractType>()
        for(possibility in possibilities)
        {
            types.add(possibility.type)
        }
        val typesArray : Array<AbstractType> = types.toTypedArray()
        return EitherType(*typesArray)
    }

}