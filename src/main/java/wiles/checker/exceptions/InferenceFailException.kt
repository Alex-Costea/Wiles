package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation

class InferenceFailException(location: TokenLocation) : AbstractCompilationException(
    "Type inference failed, due to not enough information being present. Please specify the type manually.", location)