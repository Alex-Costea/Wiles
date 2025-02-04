package wiles.parser.statements.expressions

import wiles.parser.builders.ParserContext
import wiles.shared.constants.Tokens.TYPE_ID

class TypeExpression(context: ParserContext) : AbstractExpression(context)
{
    override var name = TYPE_ID
}
