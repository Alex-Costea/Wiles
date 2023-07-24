package wiles.checker.data

import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement

data class InferrerDetails(val statement : JSONStatement,
                           val variables : CheckerVariableMap,
                           val exceptions: CompilationExceptionsCollection,
                           val additionalVars : CheckerVariableMap,
                           val context: CheckerContext)
