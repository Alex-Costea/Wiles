package wiles.parser.statements.expressions

import wiles.parser.builders.Context

class InnerDefaultExpression(oldContext: Context) : AbstractExpression(oldContext.setWithinInnerExpression(true)) {
    init {
        isInner = true
    }
}