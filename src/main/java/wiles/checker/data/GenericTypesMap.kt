package wiles.checker.data

import wiles.shared.JSONStatement

//first boolean: has this element occurred more than once?
//second boolean: was the declaration reached?
//yes it should really be an object
class GenericTypesMap : HashMap<String, Triple<JSONStatement, Boolean, Boolean>>()