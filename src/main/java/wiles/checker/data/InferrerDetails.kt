package wiles.checker.data

import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement

data class InferrerDetails(val statement : JSONStatement,
                           val variables : VariableMap,
                           val exceptions: CompilationExceptionsCollection)