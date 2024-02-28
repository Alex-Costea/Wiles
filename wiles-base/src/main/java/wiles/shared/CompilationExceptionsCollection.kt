package wiles.shared

class CompilationExceptionsCollection : ArrayList<AbstractCompilationException>() {

    override fun equals(other: Any?): Boolean {
        if (other !is CompilationExceptionsCollection)
            return false
        if (other.size != size)
            return false
        for (i in 0 until size)
            if (this[i].message != other[i].message)
                return false
        return true
    }

    override fun hashCode(): Int {
        var hashCode = 7
        for (obj in this)
            hashCode = 31 * hashCode + obj.message.hashCode()
        return hashCode
    }

    fun throwFirstIfExists()
    {
        if(size>0)
            throw this[0]
    }

}
