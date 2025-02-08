package wiles.parser.builders

import wiles.parser.services.TokenTransmitter

class ParserContext(val transmitter: TokenTransmitter) {
    var isOutermost = false
    private set
    var isWithinMethod = false
    private set
    var isWithinLoop = false
    private set

    fun setOutermost(to:Boolean) : ParserContext
    {
        if(isOutermost == to)
            return this
        val x = clone()
        x.isOutermost = to
        return x
    }

    fun setWithinMethod(to:Boolean) : ParserContext
    {
        if(isWithinMethod == to)
            return this
        val x = clone()
        x.isWithinMethod = to
        return x
    }

    fun setWithinLoop(to:Boolean) : ParserContext
    {
        if(isWithinLoop == to)
            return this
        val x = clone()
        x.isWithinLoop = to
        return x
    }

    private fun clone(): ParserContext {
        return ParserContext(transmitter)
            .setOutermost(isOutermost).setWithinLoop(isWithinLoop)
            .setWithinMethod(isWithinMethod)
    }
}