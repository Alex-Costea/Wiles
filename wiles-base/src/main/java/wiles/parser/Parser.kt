package wiles.parser

import wiles.parser.converters.TokensToSyntaxTreeConverter
import wiles.parser.statements.CodeBlockStatement
import wiles.shared.*
import wiles.shared.constants.ErrorMessages.IO_ERROR
import wiles.shared.constants.Settings.SYNTAX_TREE_FILE
import java.io.*
import java.util.*
import java.util.stream.Collectors

class Parser(content : String?, isDebug : Boolean, filename : String?) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var results : CodeBlockStatement
    val input = content?:loadFile(filename!!)
    lateinit var json : String

    init{
        val tokens = sourceToTokens(input)
        if(isDebug) {
            print("Tokens: ")
            println(tokens.stream().map(Token::content).toList())
        }

        val ast = tokensToAST(tokens, lastLocation(input))
        results = ast

        if(isDebug)
            JSONService.writeValue(File(SYNTAX_TREE_FILE), ast)
        else json = JSONService.writeValueAsString(ast)
    }

    fun getExceptions() : CompilationExceptionsCollection
    {
        return exceptions
    }

    fun getResults() : CodeBlockStatement
    {
        return results
    }

    private fun lastLocation(input : String) : TokenLocation
    {
        val textSplit = input.trimStart().split("\n")
        val lastIndex = textSplit.lastIndex
        val lastLineLocation = textSplit[lastIndex].length
        return TokenLocation(lastIndex+1,lastLineLocation+1,
            lastIndex+1,lastLineLocation+2)
    }

    private fun loadFile(filename: String): String {
        val input: String
        try {
            val resource : InputStream = File(filename).inputStream()
            resource.use { input = BufferedReader(InputStreamReader(it))
                    .lines().collect(Collectors.joining("\n"))
                return input
            }
        }
        catch (ex: IOException) {
            throw InternalErrorException(IO_ERROR + ex.message)
        }
    }

    private fun sourceToTokens(input: String): List<Token> {
        val converter = wiles.parser.converters.InputToTokensConverter(input, lastLocation(input))
        val tokens = converter.convert()
        exceptions.addAll(converter.exceptions)
        return tokens
    }

    private fun tokensToAST(tokens: List<Token>, lastLocation : TokenLocation): CodeBlockStatement {
        val converter = TokensToSyntaxTreeConverter(tokens,lastLocation)
        val programStatement = converter.convert()
        exceptions.addAll(converter.exceptions)
        return programStatement
    }
}