package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.types.FunctionCallType
import wiles.processor.values.WilesFunctionCall
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorFuncCall(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {

    override fun process(): Value {
        return Value(WilesFunctionCall(), FunctionCallType(), VariableStatus.Const)
    }
}