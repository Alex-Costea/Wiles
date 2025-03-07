package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.errors.ValueUnusedException
import wiles.processor.processors.Processor.Companion.NOTHING_VALUE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.InvalidType
import wiles.processor.utils.TypeUtils
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Tokens.LEVEL_SCOPE_ID
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.WilesException

class ProcessorCodeBlock (
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {

    override var value: Value = NOTHING_VALUE

    override fun process() {
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
                val processor = Processor(component, context)
                processor.process()
                if(context.isCompiling)
                {
                    val type = processor.value.getType()
                    if(type is InvalidType)
                        continue
                    if(!TypeUtils.isSuperType(NOTHING_TYPE, type))
                        throw ValueUnusedException(component.getFirstLocation())
                }
            }
        }
        catch (ex : WilesException)
        {
            context.exceptions.add(ex)
        }
    }
}