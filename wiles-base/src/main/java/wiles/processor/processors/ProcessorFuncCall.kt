package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueData
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.types.DataType
import wiles.processor.types.WilesType
import wiles.processor.values.WilesData
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.enums.SyntaxType


class ProcessorFuncCall(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {

    override fun process(): Value {
        val map = ValuesMap()
        var i = 0
        for(component in syntax.getComponents())
        {
            if(component.syntaxType == SyntaxType.EXPRESSION &&
                component.getComponents().getOrNull(0)?.details?.getOrNull(0) == ASSIGN_ID)
            {
                val left = component.getComponents()[1]
                val right = component.getComponents()[2]
                if(left.syntaxType != SyntaxType.TOKEN || !IS_IDENTIFIER.test(left.details[0]))
                    TODO("Simple assignment required")
                val leftName = left.details[0]
                val rightValue = Processor(right, context).process()
                map[leftName] = ValueData(rightValue, variableStatus = VariableStatus.Const,
                    comptimeType = rightValue.getType())
            }
            else{
                val value = Processor(component, context).process()
                map[i.toString()] = ValueData(value, variableStatus = VariableStatus.Const,
                    comptimeType = value.getType())
                i++
            }
        }
        val data = WilesData(map)
        return Value(data, WilesType(DataType(data)))
    }
}