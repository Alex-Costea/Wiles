package wiles.interpreter.data

import wiles.shared.WilesExceptionsCollection

class InterpreterContext(
    val isRunning : Boolean,
    val values : ValuesMap,
    val isDebug : Boolean,
    val exceptions : WilesExceptionsCollection
){
    override fun toString(): String {
        return "InterpreterContext(isRunning=$isRunning, values=$values, isDebug=$isDebug)"
    }
}