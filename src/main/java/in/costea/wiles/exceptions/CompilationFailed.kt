package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.CompilationExceptionsCollection

class CompilationFailed(exceptionsCollection: CompilationExceptionsCollection,input: String) :
    RuntimeException(exceptionsCollection.getExceptionsString(input))