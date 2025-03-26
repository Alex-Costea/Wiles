package wiles.processor.assignments

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueData
import wiles.processor.errors.CantBeModifiedException
import wiles.processor.errors.IdentifierUnknownException
import wiles.processor.errors.TypeConflictError
import wiles.processor.processors.Processor
import wiles.processor.types.AbstractType
import wiles.processor.utils.InterpreterUtils
import wiles.processor.values.WilesNothing
import wiles.processor.values.WilesUndefined
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Predicates.IS_IDENTIFIER

class IdentifierAssignmentOperation(private val leftComponent: AbstractSyntaxTree,
                                    private val rightComponent: AbstractSyntaxTree,
                                    private val context: InterpreterContext){

        private fun getValue(tree : AbstractSyntaxTree): Value {
            val innerProcessor = Processor(tree, context)
            return innerProcessor.process()
        }

        fun getNewValue(): Value {
            val name = leftComponent.details[0]

            if(!IS_IDENTIFIER.test(name))
                throw CantBeModifiedException(leftComponent.getFirstLocation())

            val leftValueData = context.values[name] ?:
                throw IdentifierUnknownException(leftComponent.getFirstLocation())
            val leftValue = leftValueData.value
            val rightValue = getValue(rightComponent)

            val leftType = leftValueData.getComptimeType()
            val rightType = rightValue.getType()

            val leftIsUndefined = leftValue.getObj() is WilesUndefined
            val leftIsVariable = leftValueData.isVariable()

            if(context.isCompiling) {
                val location = leftComponent.getFirstLocation()
                if (!leftIsVariable && !leftIsUndefined) throw CantBeModifiedException(location)
                if (!InterpreterUtils.isSuperType(leftType, rightType))
                    throw TypeConflictError(leftType, rightType, location)
            }

            val comptimeType = if(leftIsUndefined && !leftIsVariable) rightType else leftType
            val newValue = ValueData(Value(rightValue.getObj(), rightType),
                leftValueData.variableStatus, comptimeType)
            context.values[name] = newValue
            return Value(WilesNothing, AbstractType.NOTHING_TYPE)
    }
}