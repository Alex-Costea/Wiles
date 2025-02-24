package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueProps
import wiles.processor.errors.ComptimeTypeUnknownException
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.BOOLEAN_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INTEGER_TYPE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesNothing
import wiles.shared.AbstractSyntaxTree

class ProcessorTypeExpression(syntax: AbstractSyntaxTree, context: InterpreterContext)
    : ProcessorExpression(syntax, context)
{

    private fun getNewTypeObject(value : Value) : AbstractType{
        return when(val obj = value.getObj()) {
            is WilesInteger -> INTEGER_TYPE.exactly(obj)
            is WilesDecimal -> DECIMAL_TYPE.exactly(obj)
            is WilesNothing -> NOTHING_TYPE
            is String -> TEXT_TYPE.exactly(obj)
            is Boolean -> BOOLEAN_TYPE.exactly(obj)
            is AbstractType -> obj
            else -> TODO("Rest of values")
        }
    }

    override fun process() {
        assert(context.compileMode)
        if(syntax.components.size > 1)
            super.process()
        else{
            val processorExpression = ProcessorExpression(syntax.components[0], context)
            processorExpression.process()
            value = processorExpression.value
        }
        if(value.isKnown()) {
            val newValue = getNewTypeObject(value)
            value = Value(newValue, TYPE_TYPE, ValueProps.DEFAULT_EXPR)
        }
        else {
            throw ComptimeTypeUnknownException(syntax.getFirstLocation())
        }
    }
}