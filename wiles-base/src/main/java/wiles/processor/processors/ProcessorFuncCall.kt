package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.DataType
import wiles.processor.types.WilesType
import wiles.processor.values.WilesData
import wiles.shared.abstracts.AbstractSyntaxTree

class ProcessorFuncCall(syntax: AbstractSyntaxTree, context: InterpreterContext) : AbstractProcessor(syntax, context) {

    override fun process(): Value {
        return Value(WilesData(), WilesType(DataType()))
    }
}