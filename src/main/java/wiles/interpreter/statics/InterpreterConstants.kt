package wiles.interpreter.statics

import wiles.interpreter.data.ObjectDetails
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.TypeConstants
import wiles.shared.constants.TypeUtils
import wiles.shared.constants.Types

object InterpreterConstants {

    val ZERO_REF = ObjectDetails(0L, TypeConstants.INT64_TYPE)
    val MAX_INT64_REF = ObjectDetails(Long.MAX_VALUE, TypeConstants.INT64_TYPE)

    fun Long.toIntOrNull(): Int? {
        return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) this.toInt()
        else null
    }

    fun addType(resultingType: JSONStatement, addedType: JSONStatement): JSONStatement {
        return if(TypeUtils.isFormerSuperTypeOfLatter(resultingType, addedType))
            resultingType
        else if(TypeUtils.isFormerSuperTypeOfLatter(addedType, resultingType))
            addedType
        else if(resultingType.name == Types.EITHER_ID) {
            resultingType.components.add(addedType)
            resultingType
        } else JSONStatement(name = Types.EITHER_ID, syntaxType = SyntaxType.TYPE,
            components = mutableListOf(resultingType,addedType))
    }

}