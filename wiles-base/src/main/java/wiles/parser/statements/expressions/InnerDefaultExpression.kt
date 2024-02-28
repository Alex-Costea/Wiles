package wiles.parser.statements.expressions

import wiles.parser.builders.ParserContext

class InnerDefaultExpression(oldContext: ParserContext) : AbstractExpression(oldContext.setWithinInnerExpression(true)) {
    init {
        isInner = true
    }
}