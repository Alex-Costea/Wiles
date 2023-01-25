package wiles.checker

import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement

data class InferrerDetails(val statement : JSONStatement,
                      val variables : HashMap<String, VariableDetails>,
                      val exceptions: CompilationExceptionsCollection)