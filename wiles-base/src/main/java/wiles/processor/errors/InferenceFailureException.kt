package wiles.processor.errors

import wiles.shared.TokenLocation
import wiles.shared.WilesException
import wiles.shared.constants.ErrorMessages.INFERENCE_FAILED_ERROR

class InferenceFailureException(tokenLocation: TokenLocation) : WilesException(INFERENCE_FAILED_ERROR, tokenLocation)