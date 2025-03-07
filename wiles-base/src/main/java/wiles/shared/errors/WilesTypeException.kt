package wiles.shared.errors

import wiles.processor.types.AbstractType

class WilesTypeException(val type1 : AbstractType?, val type2 : AbstractType) : RuntimeException()