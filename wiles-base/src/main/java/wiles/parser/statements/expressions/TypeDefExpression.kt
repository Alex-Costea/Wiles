package wiles.parser.statements.expressions

import wiles.parser.builders.ParserContext
import wiles.shared.SyntaxType

class TypeDefExpression(context: ParserContext) : AbstractExpression(context)
{
    override val syntaxType = SyntaxType.TYPEDEF
}
