package wiles.interpreter.checker

import wiles.interpreter.JSONStatement
import wiles.shared.CompilationExceptionsCollection

class Checker(val statement : JSONStatement)
{
    fun check() : CompilationExceptionsCollection
    {
        val exceptions = CompilationExceptionsCollection()
        //TODO: check types/initializations/etc
        return exceptions
    }
}