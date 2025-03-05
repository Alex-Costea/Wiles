package wiles.processor.errors

import wiles.shared.data.TokenLocation
import wiles.shared.errors.WilesException
import wiles.shared.constants.ErrorMessages.INFERENCE_FAILED_ERROR

class InferenceFailureException(tokenLocation: TokenLocation) : WilesException(INFERENCE_FAILED_ERROR, tokenLocation)