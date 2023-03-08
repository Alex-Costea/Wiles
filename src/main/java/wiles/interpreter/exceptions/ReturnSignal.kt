package wiles.interpreter.exceptions

import wiles.interpreter.data.ObjectDetails

class ReturnSignal(val value : ObjectDetails) : Throwable()