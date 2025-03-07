package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.errors.IdentifierUnknownException
import wiles.processor.errors.ValueUndefinedException
import wiles.processor.types.*
import wiles.processor.utils.TypeUtils.getNewTypeObject
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesUndefined
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Predicates.IS_LITERAL
import wiles.shared.constants.Predicates.IS_NUMBER_LITERAL
import wiles.shared.constants.Predicates.IS_TEXT_LITERAL
import wiles.shared.enums.SyntaxType
import wiles.shared.errors.InternalErrorException
import wiles.shared.errors.WilesException

class ProcessorToken(
    syntax : AbstractSyntaxTree,
    context : InterpreterContext
) : AbstractProcessor(syntax, context) {
    override lateinit var value : Value

    private fun processNr(name: String)
    {
        val newName = name.substring(1).replace("_","")
        if(newName.contains("."))
        {
            val decimal = WilesDecimal(newName)
            value = Value(decimal, DecimalType().exactly(decimal), VariableStatus.Const)
        }
        else{
            val bigInt = WilesInteger(newName)
            value = Value(bigInt, IntType().exactly(bigInt), VariableStatus.Const)
        }
    }

    private fun processText(name: String)
    {
        val newName = name.substring(1)
        value = Value(newName, TextType().exactly(newName), VariableStatus.Const)
    }

    private fun processIdentifier(syntax: AbstractSyntaxTree) {
        try {
            val name = syntax.details[0]
            if(!context.values.containsKey(name))
            {
                value = Value(null, InvalidType(), VariableStatus.Const)
                throw IdentifierUnknownException(syntax.getFirstLocation())
            }
            val newValue = context.values[name]!!
            if(context.values[name]?.getObj() is WilesUndefined)
                throw ValueUndefinedException(syntax.getFirstLocation())
            value = Value(newValue.getObj(), getType(newValue), VariableStatus.Const)

        }
        catch (ex : WilesException)
        {
            value = Value(null, InvalidType(), VariableStatus.Const)
            throw ex
        }

    }

    private fun getType(newValue: Value): AbstractType {
        if(newValue.getObj() is AbstractType)
            return AbstractType.TYPE_TYPE
        return getNewTypeObject(newValue)
    }

    override fun process() {
        assert(syntax.syntaxType == SyntaxType.TOKEN)
        val name = syntax.details[0]
        assert(IS_LITERAL.test(name))
        if(IS_NUMBER_LITERAL.test(name))
            processNr(name)
        else if(IS_TEXT_LITERAL.test(name))
            processText(name)
        else if(IS_IDENTIFIER.test(name))
            processIdentifier(syntax)
        else throw InternalErrorException()
    }
}