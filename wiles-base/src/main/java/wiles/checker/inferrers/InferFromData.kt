package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens

class InferFromData(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        assert(statement.syntaxType== SyntaxType.DATA)
        val inferredType = JSONStatement(name = Tokens.DATA_ID, syntaxType = SyntaxType.TYPE)
        var isKey = true
        for(component in statement.components)
        {
            if(isKey)
            {
                val name = component.components[0].name
                inferredType.components.add(
                    JSONStatement(
                        Tokens.IDENTIFIER_START + name.substring(1),
                        syntaxType = SyntaxType.TOKEN
                    )
                )
            }
            else
            {
                val inferrer = InferFromExpression(
                    InferrerDetails(component, variables, exceptions)
                )
                inferrer.infer()
                inferredType.components.add(component.components[0].copyRemovingLocation())
            }
            isKey = !isKey
        }
        statement.components.add(0, inferredType)
    }
}