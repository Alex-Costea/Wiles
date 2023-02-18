package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class InferenceFailException(location: TokenLocation,
    reason: String = "not enough information being present. Please specify the type manually.") : AbstractCompilationException(
    "Type inference failed, due to $reason", location)