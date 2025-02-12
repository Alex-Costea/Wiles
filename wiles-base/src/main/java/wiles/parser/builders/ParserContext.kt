package wiles.parser.builders

import wiles.parser.services.TokenTransmitter

class ParserContext(val transmitter: TokenTransmitter) {
    var isWithinMethod = false
    private set

    fun setWithinMethod(to:Boolean) : ParserContext
    {
        if(isWithinMethod == to)
            return this
        val x = clone()
        x.isWithinMethod = to
        return x
    }

    private fun clone(): ParserContext {
        return ParserContext(transmitter).setWithinMethod(isWithinMethod)
    }
}