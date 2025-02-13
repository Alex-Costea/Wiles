package wiles.interpreter

class InterpreterContext(
    val isRunning : Boolean,
    val values : ValuesMap,
    val isDebug : Boolean
){
    override fun toString(): String {
        return "InterpreterContext(isRunning=$isRunning, values=$values, isDebug=$isDebug)"
    }
}