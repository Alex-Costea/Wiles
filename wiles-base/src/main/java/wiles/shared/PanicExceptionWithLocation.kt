package wiles.shared

class PanicExceptionWithLocation(s: String, tokenLocation: TokenLocation) :
    AbstractCompilationException(s, tokenLocation)