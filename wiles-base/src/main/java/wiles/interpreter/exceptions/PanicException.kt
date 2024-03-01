package wiles.interpreter.exceptions

import wiles.shared.TokenLocation

class PanicException : Exception {
    var location: TokenLocation?

    constructor(s: String, location: TokenLocation? = null) : super(s) {
        this.location = location
    }

    constructor(location: TokenLocation? = null) : super() {
        this.location = location
    }
}