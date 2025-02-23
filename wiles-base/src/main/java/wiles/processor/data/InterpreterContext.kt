package wiles.processor.data

import wiles.shared.WilesExceptionsCollection

class InterpreterContext(
    val isRunning : Boolean,
    val values : ValuesMap,
    val isDebug : Boolean,
    val exceptions : WilesExceptionsCollection
){
    val compileMode = !isRunning
    override fun toString(): String {
        return "InterpreterContext(isRunning=$isRunning, values=$values, isDebug=$isDebug)"
    }
}