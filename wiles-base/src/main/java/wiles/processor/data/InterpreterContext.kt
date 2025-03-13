package wiles.processor.data

import wiles.shared.data.WilesExceptionsCollection

class InterpreterContext(
    val values : ValuesMap,
    val isRunning : Boolean,
    val exceptions : WilesExceptionsCollection
){
    val isCompiling = !isRunning
    override fun toString(): String {
        return "InterpreterContext(values=$values, isRunning=$isRunning, exceptions=$exceptions)"
    }
}