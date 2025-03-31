package wiles.shared.errors

import wiles.processor.types.WilesType

class WilesTypeException(val type1 : WilesType?, val type2 : WilesType) : RuntimeException()