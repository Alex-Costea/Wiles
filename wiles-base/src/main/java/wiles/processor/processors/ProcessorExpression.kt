package wiles.processor.processors

import wiles.processor.assignments.IdentifierAssignmentOperation
import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.CantBeModifiedException
import wiles.processor.errors.StackOverflowException
import wiles.processor.operations.ApplyOperation
import wiles.processor.operations.InternalOperation
import wiles.processor.operations.PlusOperation
import wiles.processor.operations.UnionOperation
import wiles.processor.types.FunctionCallType
import wiles.processor.types.InvalidType
import wiles.processor.values.WilesFunctionCall
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ACCESS_ID
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.AS_ID
import wiles.shared.constants.Tokens.AT_KEY_ID
import wiles.shared.constants.Tokens.DIVIDE_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.INTERNAL_ID
import wiles.shared.constants.Tokens.LARGER_EQUALS_ID
import wiles.shared.constants.Tokens.LARGER_ID
import wiles.shared.constants.Tokens.MAYBE_ID
import wiles.shared.constants.Tokens.MINUS_ID
import wiles.shared.constants.Tokens.MUTIFY_ID
import wiles.shared.constants.Tokens.NOT_EQUAL_ID
import wiles.shared.constants.Tokens.NOT_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.Tokens.PLUS_ID
import wiles.shared.constants.Tokens.POWER_ID
import wiles.shared.constants.Tokens.RANGIFY_ID
import wiles.shared.constants.Tokens.SMALLER_EQUALS_ID
import wiles.shared.constants.Tokens.SMALLER_ID
import wiles.shared.constants.Tokens.SUBTYPES_ID
import wiles.shared.constants.Tokens.TIMES_ID
import wiles.shared.constants.Tokens.UNARY_MINUS_ID
import wiles.shared.constants.Tokens.UNARY_PLUS_ID
import wiles.shared.constants.Tokens.UNION_ID
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.InternalErrorException
import wiles.shared.errors.WilesException

open class ProcessorExpression(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
): AbstractProcessor(syntax, context){

    lateinit var value : Value

    private fun getValue(tree : AbstractSyntaxTree?): Value? {
        if(tree == null) return null
        val innerProcessorExpression = ProcessorExpression(tree, context)
        innerProcessorExpression.process()
        return innerProcessorExpression.value
    }

    override fun process() {

        when (syntax.syntaxType) {
            SyntaxType.TOKEN -> {
                val processorToken = ProcessorToken(syntax, context)
                processorToken.process()
                value = processorToken.value
            }
            SyntaxType.FUNC_CALL -> {
                //TODO: Handle complex function calls
                value = Value(WilesFunctionCall(), FunctionCallType(), VariableStatus.Const)
            }
            else -> {
                try{
                    val operationType = syntax.getComponents()[0].details[0]
                    val leftComponent = syntax.getComponents().getOrNull(1)
                    val rightComponent = syntax.getComponents().getOrNull(2)
                    if(operationType == ASSIGN_ID)
                    {
                        val operation = if(leftComponent!!.syntaxType == SyntaxType.TOKEN) {
                            val name = leftComponent.details[0]
                            if(IS_IDENTIFIER.test(name))
                                IdentifierAssignmentOperation(leftComponent, rightComponent!!, context)
                            else throw CantBeModifiedException(leftComponent.getFirstLocation())
                        }
                        else TODO("Handling mutable collections")
                        value = operation.getNewValue()
                        return
                    }
                    val left = getValue(leftComponent)
                    val right = getValue(rightComponent)
                    if(context.exceptions.isNotEmpty())
                        return
                    val operand = when(operationType)
                    {
                        PLUS_ID -> PlusOperation(left!!, right!!, context)
                        MINUS_ID -> TODO("Implement MinusOperation")
                        UNARY_PLUS_ID -> TODO("Implement UnaryPlusOperation")
                        UNARY_MINUS_ID -> TODO("Implement UnaryMinusOperation")
                        MUTIFY_ID -> TODO("Implement MutifyOperation")
                        TIMES_ID -> TODO("Implement TimesOperation")
                        DIVIDE_ID -> TODO("Implement DivideOperation")
                        POWER_ID -> TODO("Implement PowerOperation")
                        MAYBE_ID -> TODO("Implement MaybeOperation")
                        ACCESS_ID -> TODO("Implement AccessOperation")
                        AT_KEY_ID -> TODO("Implement AtKeyOperation")
                        APPLY_ID -> ApplyOperation(left, right!!, context)
                        OR_ID -> TODO("Implement OrOperation")
                        AND_ID -> TODO("Implement AndOperation")
                        NOT_ID -> TODO("Implement NotOperation")
                        SUBTYPES_ID -> TODO("Implement SubtypesOperation")
                        UNION_ID -> UnionOperation(left!!, right!!, context)
                        EQUALS_ID -> TODO("Implement EqualsOperation")
                        NOT_EQUAL_ID -> TODO("Implement NotEqualOperation")
                        LARGER_ID -> TODO("Implement LargerOperation")
                        SMALLER_ID -> TODO("Implement SmallerOperation")
                        LARGER_EQUALS_ID -> TODO("Implement LargerEqualsOperation")
                        SMALLER_EQUALS_ID -> TODO("Implement SmallerEqualsOperation")
                        RANGIFY_ID -> TODO("Implement RangifyOperation")
                        AS_ID -> TODO("Implement AsOperation")
                        INTERNAL_ID -> InternalOperation(left!!, context)
                        else -> throw InternalErrorException("Unknown operation")
                    }
                    value = operand.getNewValue()
                }
                catch (ex : WilesException)
                {
                    value = Value(null, InvalidType(), VariableStatus.Const)
                    throw ex
                }
                catch(ex : StackOverflowError)
                {
                    value = Value(null, InvalidType(), VariableStatus.Const)
                    throw StackOverflowException(syntax.getFirstLocation())
                }

            }
        }
    }

}