package wiles.parser

import wiles.Main.DEBUG
import wiles.Main.filename
import wiles.parser.converters.TokensToSyntaxTreeConverter
import wiles.parser.statements.CodeBlockStatement
import wiles.shared.*
import wiles.shared.constants.ErrorMessages.IO_ERROR
import wiles.shared.constants.Settings.SYNTAX_TREE_FILE
import java.io.*
import java.util.*
import java.util.stream.Collectors

class Parser(content : String?) {
    private val exceptions: CompilationExceptionsCollection = CompilationExceptionsCollection()
    private var results : CodeBlockStatement
    val input = content?:loadFile(filename)
    lateinit var json : String
    var additionalLines = 0

    init{
        val tokens = sourceToTokens(input)
        if(DEBUG) {
            print("Tokens: ")
            println(tokens.stream().map(Token::content).toList())
        }

        val ast = tokensToAST(tokens, lastLocation(input))
        results = ast

        if(DEBUG)
            JsonService.writeValue(File(SYNTAX_TREE_FILE), ast)
        else json = JsonService.writeValueAsString(ast)
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
        return TokenLocation(lastIndex+1,lastLineLocation+2)
    }

    private fun loadFile(filename: String): String {
        val input: String
        try {
            val resource : InputStream = File(filename).inputStream()
            resource.use { input = BufferedReader(InputStreamReader(it))
                    .lines().collect(Collectors.joining("\n"))
                return readStandardLibrary() + input
            }
        }
        catch (ex: IOException) {
            throw InternalErrorException(IO_ERROR)
        }
    }

    private fun readStandardLibrary(): String {
        val classloader = Thread.currentThread().contextClassLoader
        val input: String
        try {
            classloader.getResourceAsStream("additional_code.wiles").use { inputStream ->
                Objects.requireNonNull(inputStream)
                input = BufferedReader(InputStreamReader(inputStream!!))
                    .lines().collect(Collectors.joining("\n"))
                additionalLines=input.count { it == '\n' }
                return input
            }
        } catch (ex: NullPointerException) {
            throw InternalErrorException(IO_ERROR)
        } catch (ex: IOException) {
            throw InternalErrorException(IO_ERROR)
        }
    }

    private fun sourceToTokens(input: String): List<Token> {
        val converter = wiles.parser.converters.InputToTokensConverter(input,additionalLines)
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