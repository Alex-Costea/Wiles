package `in`.costea.wiles.exceptions

import `in`.costea.wiles.data.CompilationExceptionsCollection

class CompilationFailed(exceptionsCollection: CompilationExceptionsCollection) : RuntimeException(exceptionsCollection.getExceptionsString())