package wiles.checker.exceptions

import wiles.shared.AbstractCompilationException
import wiles.shared.TokenLocation
import wiles.shared.constants.ErrorMessages.TYPE_INFERENCE_FAIL_ERROR

class InferenceFailException(location: TokenLocation) : AbstractCompilationException(
    TYPE_INFERENCE_FAIL_ERROR, location)