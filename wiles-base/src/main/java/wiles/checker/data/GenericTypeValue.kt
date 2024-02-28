package wiles.checker.data

import wiles.shared.JSONStatement

class GenericTypeValue(var statement : JSONStatement,
                       var occurredMultipleTimes : Boolean,
                       var declarationReached : Boolean)