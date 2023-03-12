package wiles.interpreter.statics

object InterpreterConstants {

    fun Long.toIntOrNull(): Int? {
        return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) this.toInt()
        else null
    }

}