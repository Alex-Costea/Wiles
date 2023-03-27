package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.TYPEDEF_ID
import wiles.shared.constants.Types.TYPE_TYPE_ID

class InferFromTypeDef(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        val name = statement.components[0].name
        val type = statement.components[1]
        val typeType = JSONStatement(name = TYPE_TYPE_ID, syntaxType = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
        variables[name] = VariableDetails(typeType)
        variables["$name|$TYPEDEF_ID"] = VariableDetails(typeType)
    }
}