package `in`.costea.wiles.data

import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.statics.Constants.DEBUG

class CompilationExceptionsCollection : ArrayList<AbstractCompilationException>() {
    fun getExceptionsString(): String {
        val optional: String = sortedWith(nullsLast(compareBy<AbstractCompilationException> { it.tokenLocation?.line }
                .thenBy { it.tokenLocation?.lineIndex }))
                .map { "\n    " + it.message + "\n" + (if (DEBUG) it.stackTraceToString() else "") }
                .fold("") { a, b -> a + b }
        if (optional.isEmpty())
            throw IllegalStateException()
        return optional
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CompilationExceptionsCollection)
            return false
        for (i in 0 until other.size)
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

}
