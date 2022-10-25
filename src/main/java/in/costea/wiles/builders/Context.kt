package `in`.costea.wiles.builders

import `in`.costea.wiles.services.TokenTransmitter

class Context(val transmitter: TokenTransmitter) {
    var isOutermost = false
    private set
    var isWithinMethod = false
    private set
    var isWithinLoop = false
    private set

    fun setOutermost() : Context
    {
        if(isOutermost)
            return this
        val x = clone()
        x.isOutermost = true
        return x
    }

    fun setWithinMethod() : Context
    {
        if(isWithinMethod)
            return this
        val x = clone()
        x.isWithinMethod = true
        return x
    }

    fun setWithinLoop() : Context
    {
        if(isWithinLoop)
            return this
        val x = clone()
        x.isWithinLoop = true
        return x
    }

    private fun clone(): Context {
        val x = Context(transmitter)
        if(isOutermost)
            x.isOutermost = true
        if(isWithinLoop)
            x.isOutermost = true
        if(isWithinLoop)
            x.isWithinLoop = true
        return x
    }
}