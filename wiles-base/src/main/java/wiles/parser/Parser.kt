package wiles.parser

import wiles.parser.converters.TokensToSyntaxTreeConverter
import wiles.parser.statements.CodeBlockStatement
import wiles.shared.Token
import wiles.shared.TokenLocation
import wiles.shared.WilesExceptionsCollection

class Parser(private val input : String, isDebug : Boolean) {
    private val exceptions: WilesExceptionsCollection = WilesExceptionsCollection()
    private var results : CodeBlockStatement

    init{
        val tokens = sourceToTokens()
        if(isDebug) {
            print("Tokens: ")
            println(tokens.stream().map(Token::content).toList())
        }

        val ast = tokensToAST(tokens, lastLocation())
        results = ast
    }

    fun getExceptions() : WilesExceptionsCollection
    {
        return exceptions
    }

    fun getResults() : CodeBlockStatement
    {
        return results
    }

    private fun lastLocation() : TokenLocation
    {
        val textSplit = input.trimStart().split("\n")
        val lastIndex = textSplit.lastIndex
        val lastLineLocation = textSplit[lastIndex].length
        return TokenLocation(lastIndex+1,lastLineLocation+1,
            lastIndex+1,lastLineLocation+2)
    }

    private fun sourceToTokens(): List<Token> {
        val converter = wiles.parser.converters.InputToTokensConverter(input, lastLocation())
        val tokens = converter.convert()
        exceptions.addAll(converter.getExceptions())
        return tokens
    }

    private fun tokensToAST(tokens: List<Token>, lastLocation : TokenLocation): CodeBlockStatement {
        val converter = TokensToSyntaxTreeConverter(tokens,lastLocation)
        val programStatement = converter.convert()
        exceptions.addAll(converter.exceptions)
        return programStatement
    }
}