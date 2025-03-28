package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.YieldedValue
import wiles.processor.errors.UnreachableCodeException
import wiles.processor.errors.ValueUnusedException
import wiles.processor.processors.Processor.Companion.NOTHING_VALUE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.InvalidType
import wiles.processor.utils.InterpreterUtils
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.WilesException

class ProcessorCodeBlock (
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    override fun process() : Value {
        var finalValue : Value? = null
        try {
            if(context.isCompiling){
                for (component in syntax.getComponents())
                {
                    if(component.syntaxType == SyntaxType.DECLARATION && component.details.contains(LEVEL_SCOPE_ID)) {
                        val processor = ProcessorDeclaration(component, context)
                        processor.process()
                    }
                }
            }
            for (component in syntax.getComponents()) {
                if(finalValue != null)
                    throw UnreachableCodeException(component.getFirstLocation())
                val processor = Processor(component, context)
                val value = processor.process()
                if(value is YieldedValue) {
                    finalValue = value
                }
                else if(context.isCompiling)
                {
                    val type = value.getType()
                    if(type is InvalidType)
                        continue
                    if(!InterpreterUtils.isSuperType(NOTHING_TYPE, type))
                        throw ValueUnusedException(component.getFirstLocation())
                }
            }
        }
        catch (ex : WilesException)
        {
            context.exceptions.add(ex)
        }
        return finalValue ?: NOTHING_VALUE
    }
}