package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.FunctionCallType
import wiles.processor.types.WilesType
import wiles.processor.values.WilesFunctionCall
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorFuncCall(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {

    override fun process(): Value {
        return Value(WilesFunctionCall(), WilesType(FunctionCallType()))
    }
}