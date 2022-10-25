package `in`.costea.wiles.builders

class IsWithin {
    var isOutermost = false
    private set
    var isWithinMethod = false
    private set
    var isWithinLoop = false
    private set
    fun outermost() : IsWithin
    {
        if(isOutermost)
            return this
        val x = clone()
        x.isOutermost = true
        return x
    }

    fun withinMethod() : IsWithin
    {
        if(isWithinMethod)
            return this
        val x = clone()
        x.isWithinMethod = true
        return x
    }

    fun withinLoop() : IsWithin
    {
        if(isWithinLoop)
            return this
        val x = clone()
        isWithinLoop = true
        return x
    }

    private fun clone(): IsWithin {
        val x = IsWithin()
        if(isOutermost)
            x.isOutermost = true
        if(isWithinLoop)
            x.isOutermost = true
        if(isWithinLoop)
            x.isWithinLoop = true
        return x
    }
}