package `in`.costea.wiles.builders

import `in`.costea.wiles.services.TokenTransmitter

class Context(val transmitter: TokenTransmitter) {
    var isOutermost = false
    private set
    var isWithinMethod = false
    private set
    var isWithinLoop = false
    private set

    fun setOutermost(to:Boolean) : Context
    {
        if(isOutermost == to)
            return this
        val x = clone()
        x.isOutermost = to
        return x
    }

    fun setWithinMethod(to:Boolean) : Context
    {
        if(isWithinMethod == to)
            return this
        val x = clone()
        x.isWithinMethod = to
        return x
    }

    fun setWithinLoop(to:Boolean) : Context
    {
        if(isWithinLoop == to)
            return this
        val x = clone()
        x.isWithinLoop = to
        return x
    }

    private fun clone(): Context {
        return Context(transmitter).setOutermost(isOutermost).setWithinLoop(isWithinLoop).setWithinMethod(isWithinMethod)
    }
}