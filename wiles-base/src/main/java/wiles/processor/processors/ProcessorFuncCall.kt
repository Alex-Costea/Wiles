package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.types.FunctionCallType
import wiles.processor.values.WilesFunctionCall
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorFuncCall(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {
    override lateinit var value: Value

    override fun process() {
        value = Value(WilesFunctionCall(), FunctionCallType(), VariableStatus.Const)
    }
}