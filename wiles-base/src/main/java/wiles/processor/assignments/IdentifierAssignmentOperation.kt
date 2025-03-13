package wiles.processor.assignments

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
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

            val leftValue = context.values[name] ?: throw IdentifierUnknownException(leftComponent.getFirstLocation())
            val rightValue = getValue(rightComponent)

            val leftType = leftValue.getComptimeType()
            val rightType = rightValue.getType()

            val leftIsUndefined = leftValue.getObj() is WilesUndefined
            val leftIsVariable = leftValue.isVariable()

            if(context.isCompiling) {
                val location = leftComponent.getFirstLocation()
                if (!leftIsVariable && !leftIsUndefined) throw CantBeModifiedException(location)
                if (!InterpreterUtils.isSuperType(leftType, rightType))
                    throw TypeConflictError(leftType, rightType, location)
            }

            val newValue = Value(if(leftIsVariable) VariableStatus.Var else VariableStatus.Const,
                rightValue.getObj(), rightType,
                if(leftIsUndefined && !leftIsVariable) rightType else leftType)
            context.values[name] = newValue
            return Value(VariableStatus.Const, WilesNothing, AbstractType.NOTHING_TYPE)
    }
}