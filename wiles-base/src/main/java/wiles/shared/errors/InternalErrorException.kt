package wiles.shared.errors

import wiles.shared.constants.ErrorMessages

open class InternalErrorException : RuntimeException {
    constructor(message: String) : super(ErrorMessages.INTERNAL_ERROR + message)

    constructor() : super(ErrorMessages.INTERNAL_ERROR)
}
