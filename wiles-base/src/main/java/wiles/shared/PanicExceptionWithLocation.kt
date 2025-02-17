package wiles.shared

class PanicExceptionWithLocation(s: String, tokenLocation: TokenLocation) :
    WilesException(s, tokenLocation)