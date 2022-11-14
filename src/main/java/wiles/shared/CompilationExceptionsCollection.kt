package wiles.shared

import wiles.shared.constants.ErrorMessages.COMPILATION_FAILED_ERROR
import wiles.shared.constants.ErrorMessages.LINE_SYMBOL
import wiles.shared.constants.Settings.DEBUG

class CompilationExceptionsCollection : ArrayList<AbstractCompilationException>() {
    fun getExceptionsString(input: String): String {
        val optional = sortedWith(nullsLast(compareBy<AbstractCompilationException> { it.tokenLocation.line }
            .thenBy { it.tokenLocation.lineIndex }))
            .map { LINE_SYMBOL+ "Line ${it.tokenLocation.line}: " + it.message +
                    it.tokenLocation.displayLocation(input) + (if (DEBUG) "\n"+it.stackTraceToString() else "") }
            .fold("") { a, b -> a + b }
        if (optional.isEmpty())
            throw InternalErrorException()
        return COMPILATION_FAILED_ERROR+optional
    }

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
